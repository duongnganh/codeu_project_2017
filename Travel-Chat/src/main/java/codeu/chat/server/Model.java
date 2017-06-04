package codeu.chat.server;

import java.util.Comparator;

import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Group;
import codeu.chat.common.GroupSummary;
import codeu.chat.common.LinearUuidGenerator;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.Store;
import codeu.chat.util.store.StoreAccessor;

//for big table
import com.google.cloud.bigtable.hbase.BigtableConfiguration;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public final class Model {

  private static final Comparator<Uuid> UUID_COMPARE = new Comparator<Uuid>() {

    @Override
    public int compare(Uuid a, Uuid b) {

      if (a == b) { return 0; }

      if (a == null && b != null) { return -1; }

      if (a != null && b == null) { return 1; }

      final int order = Integer.compare(a.id(), b.id());
      return order == 0 ? compare(a.root(), b.root()) : order;
    }
  };

  private static final Comparator<Time> TIME_COMPARE = new Comparator<Time>() {
    @Override
    public int compare(Time a, Time b) {
      return a.compareTo(b);
    }
  };

  private static final Comparator<String> STRING_COMPARE = String.CASE_INSENSITIVE_ORDER;

  // note: change final to none final
  private Store<Uuid, User> userById = new Store<>(UUID_COMPARE);
  private Store<Time, User> userByTime = new Store<>(TIME_COMPARE);
  private Store<String, User> userByText = new Store<>(STRING_COMPARE);

  private Store<Uuid, Conversation> conversationById = new Store<>(UUID_COMPARE);
  private Store<Time, Conversation> conversationByTime = new Store<>(TIME_COMPARE);
  private Store<String, Conversation> conversationByText = new Store<>(STRING_COMPARE);

  private Store<Uuid, Group> groupById = new Store<>(UUID_COMPARE);
  private Store<Time, Group> groupByTime = new Store<>(TIME_COMPARE);
  private Store<String, Group> groupByText = new Store<>(STRING_COMPARE);

  private Store<Uuid, Message> messageById = new Store<>(UUID_COMPARE);
  private Store<Time, Message> messageByTime = new Store<>(TIME_COMPARE);
  private Store<String, Message> messageByText = new Store<>(STRING_COMPARE);

  private final Uuid.Generator userGenerations = new LinearUuidGenerator(null, 1, Integer.MAX_VALUE);
  private Uuid currentUserGeneration = userGenerations.make();

  private String userTable;
  private String conversationTable;
  private String groupTable;
  private String messageTable;
  private String[] tableNames;

  private String projectId;
  private String instanceId;
  private static final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("cf1");

  private boolean ifUserUpdated = true;
  private boolean ifConversationUpdated = true;
  private boolean ifGroupUpdated = true;
  private boolean ifMessageUpdated = true;

  public Model(String projectId, String instanceId, String[] tableNames){
    this.projectId = projectId;
    this.instanceId = instanceId;

    this.userTable = tableNames[0];
    this.conversationTable = tableNames[1];
    this.groupTable = tableNames[2];
    this.messageTable = tableNames[3];
    this.tableNames = tableNames;
  }

  public void refresh(){
    //Delete all contents. For test purpose
    if (ifUserUpdated) updateUsers();
    if (ifConversationUpdated) updateConversations();
    if (ifGroupUpdated) updateGroups();
    if (ifMessageUpdated) updateMessages();

    for (User user : userByText.all())
      remove(user);
    for (Conversation conversation : conversationByText.all())
      remove(conversation);
    for (Group group : groupByText.all())
      remove(group);
    for (Message message : messageByText.all())
      remove(message);

    ifUserUpdated = false;
    ifConversationUpdated = false;
    ifGroupUpdated = false;
    ifMessageUpdated = false;
  }
  // ================================= USER ========================================

  public void add(User user) {
    currentUserGeneration = userGenerations.make();

    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(userTable)));

      String rowKey = user.name;

      Put put = new Put(Bytes.toBytes(rowKey));
      //Uuid id, String name, Time creation, String nickname
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("id"), Bytes.toBytes(Uuid.toUuidString(user.id)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("name"), Bytes.toBytes(user.name));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("creation"), Bytes.toBytes(user.creation.inMs()));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("nickname"), Bytes.toBytes(user.nickname));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("password"), Bytes.toBytes(user.password));
      table.put(put);

    } catch (IOException e) {
      System.err.println("Exception while running Bigtable: " + e.getMessage());
      e.printStackTrace();
    }
    ifUserUpdated = true;
  }

  public void remove(User user) {

    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(userTable)));

      String rowKey = user.name;
      System.out.println("delete "+rowKey);
      Delete delete = new Delete(Bytes.toBytes(rowKey));
      table.delete(delete);

    } catch (IOException e) {
      System.err.println("Exception while running Bigtable: " + e.getMessage());
      e.printStackTrace();
    }
    ifUserUpdated = true;
  }

  public StoreAccessor<Uuid, User> userById() {
    if (ifUserUpdated) updateUsers();
    return userById;
  }

  public StoreAccessor<Time, User> userByTime() {
    if (ifUserUpdated) updateUsers();
    return userByTime;
  }

  public StoreAccessor<String, User> userByText() {
    if (ifUserUpdated) updateUsers();
    return userByText;
  }

  private void updateUsers(){
    userById = new Store<>(UUID_COMPARE);
    userByTime = new Store<>(TIME_COMPARE);
    userByText = new Store<>(STRING_COMPARE);
    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(userTable)));
      Scan scan = new Scan();

      System.out.println("Scan for all users:");
      ResultScanner scanner = table.getScanner(scan);
      for (Result row : scanner) {
        Uuid id = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("id"))));
        String name = Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("name")));
        Time creation = Time.fromMs(Bytes.toLong(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("creation"))));
        String nickname = Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("nickname")));
        String password = Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("password")));

        User user = new User(id, name, creation, nickname);
        user.setPassword(password);
        userById.insert(user.id, user);
        userByTime.insert(user.creation, user);
        userByText.insert(user.name, user);
      }

    } catch (IOException e) {
      System.err.println("Exception while running GetUsers in Model: " + e.getMessage());
      e.printStackTrace();
    }
    ifUserUpdated = false;
  }

  public Uuid userGeneration() {
    return currentUserGeneration;
  }

  // ================================= CONVERSATION ========================================

  public void add(Conversation conversation) {

    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(conversationTable)));

      String rowKey = conversation.title;

      Put put = new Put(Bytes.toBytes(rowKey));
      //Uuid id, Uuid owner, Uuid group, Time creation, String title
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("id"), Bytes.toBytes(Uuid.toUuidString(conversation.id)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("owner"), Bytes.toBytes(Uuid.toUuidString(conversation.owner)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("group"), Bytes.toBytes(Uuid.toUuidString(conversation.group)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("creation"), Bytes.toBytes(conversation.creation.inMs()));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("title"), Bytes.toBytes(conversation.title));
      table.put(put);

    } catch (IOException e) {
      System.err.println("Exception while running Bigtable: " + e.getMessage());
      e.printStackTrace();
    }
    ifConversationUpdated = true;
  }

  public void remove(Conversation conversation) {
    
    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(conversationTable)));

      String rowKey = conversation.title;
      Delete delete = new Delete(Bytes.toBytes(rowKey));
      table.delete(delete);

    } catch (IOException e) {
      System.err.println("Exception while running Bigtable: " + e.getMessage());
      e.printStackTrace();
    }
    ifConversationUpdated = true;
  }

  public StoreAccessor<Uuid, Conversation> conversationById() {
    if (ifConversationUpdated) updateConversations();
    return conversationById;
  }

  public StoreAccessor<Time, Conversation> conversationByTime() {
    if (ifConversationUpdated) updateConversations();
    return conversationByTime;
  }

  public StoreAccessor<String, Conversation> conversationByText() {
    if (ifConversationUpdated) updateConversations();
    return conversationByText;
  }

  private void updateConversations(){

    conversationById = new Store<>(UUID_COMPARE);
    conversationByTime = new Store<>(TIME_COMPARE);
    conversationByText = new Store<>(STRING_COMPARE);

    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(conversationTable)));
      Scan scan = new Scan();

      System.out.println("Scan for all conversations:");
      ResultScanner scanner = table.getScanner(scan);
      //Uuid id, Uuid owner, Uuid group, Time creation, String title
      for (Result row : scanner) {
        Uuid id = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("id"))));
        Uuid owner = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("owner"))));
        Uuid group = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("group"))));
        Time creation = Time.fromMs(Bytes.toLong(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("creation"))));
        String title = Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("title")));

        Conversation conversation = new Conversation(id, owner, group, creation, title);
        conversationById.insert(conversation.id, conversation);
        conversationByTime.insert(conversation.creation, conversation);
        conversationByText.insert(conversation.title, conversation);
      }

    } catch (IOException e) {
      System.err.println("Exception while running GetUsers in Model: " + e.getMessage());
      e.printStackTrace();
    }
    ifConversationUpdated = false;
  }

  // ================================= GROUP ========================================

  public void add(Group group) {

    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(groupTable)));

      String rowKey = group.title;

      Put put = new Put(Bytes.toBytes(rowKey));
      // Uuid id, Uuid owner, Time creation, String title
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("id"), Bytes.toBytes(Uuid.toUuidString(group.id)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("owner"), Bytes.toBytes(Uuid.toUuidString(group.owner)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("creation"), Bytes.toBytes(group.creation.inMs()));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("title"), Bytes.toBytes(group.title));
      table.put(put);

    } catch (IOException e) {
      System.err.println("Exception while running Bigtable: " + e.getMessage());
      e.printStackTrace();
    }
    ifGroupUpdated = true;
  }

  public void remove(Group group) {
    
    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(groupTable)));

      String rowKey = group.title;
      Delete delete = new Delete(Bytes.toBytes(rowKey));
      table.delete(delete);

    } catch (IOException e) {
      System.err.println("Exception while running Bigtable: " + e.getMessage());
      e.printStackTrace();
    }
    ifGroupUpdated = true;
  }

  public StoreAccessor<Uuid, Group> groupById() {
    if (ifGroupUpdated) updateGroups();
    return groupById;
  }

  public StoreAccessor<Time, Group> groupByTime() {
    if (ifGroupUpdated) updateGroups();
    return groupByTime;
  }

  public StoreAccessor<String, Group> groupByText() {
    if (ifGroupUpdated) updateGroups();
    return groupByText;
  }

  private void updateGroups(){

    groupById = new Store<>(UUID_COMPARE);
    groupByTime = new Store<>(TIME_COMPARE);
    groupByText = new Store<>(STRING_COMPARE);

    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(groupTable)));
      Scan scan = new Scan();

      System.out.println("Scan for all groups:");
      ResultScanner scanner = table.getScanner(scan);
      //Uuid id, Uuid owner, Time creation, String title
      for (Result row : scanner) {
        Uuid id = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("id"))));
        Uuid owner = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("owner"))));
        Time creation = Time.fromMs(Bytes.toLong(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("creation"))));
        String title = Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("title")));

        Group group = new Group(id, owner, creation, title);
        groupById.insert(group.id, group);
        groupByTime.insert(group.creation, group);
        groupByText.insert(group.title, group);
      }

    } catch (IOException e) {
      System.err.println("Exception while running GetUsers in Model: " + e.getMessage());
      e.printStackTrace();
    }
    ifGroupUpdated = false;
  }

  // ================================= MESSAGE ========================================

  public void add(Message message) {

    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(messageTable)));

      String rowKey = Uuid.toUuidString(message.id);

      Put put = new Put(Bytes.toBytes(rowKey));
      // Uuid id, Uuid next, Uuid previous, Time creation, Uuid author, String content
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("id"), Bytes.toBytes(Uuid.toUuidString(message.id)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("next"), Bytes.toBytes(Uuid.toUuidString(message.next)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("previous"), Bytes.toBytes(Uuid.toUuidString(message.previous)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("creation"), Bytes.toBytes(message.creation.inMs()));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("author"), Bytes.toBytes(Uuid.toUuidString(message.author)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("conversation"), Bytes.toBytes(Uuid.toUuidString(message.conversation)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("group"), Bytes.toBytes(Uuid.toUuidString(message.group)));
      put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("content"), Bytes.toBytes(message.content));
      table.put(put);

    } catch (IOException e) {
      System.err.println("Exception while running Bigtable: " + e.getMessage());
      e.printStackTrace();
    }
    ifMessageUpdated = true;
  }

  public void remove(Message message) {
    
    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(messageTable)));

      String rowKey = Uuid.toUuidString(message.id);
      Delete delete = new Delete(Bytes.toBytes(rowKey));
      table.delete(delete);

    } catch (IOException e) {
      System.err.println("Exception while running Bigtable: " + e.getMessage());
      e.printStackTrace();
    }
    ifMessageUpdated = true;
  }

  public StoreAccessor<Uuid, Message> messageById() {
    if (ifMessageUpdated) updateMessages();
    return messageById;
  }

  public StoreAccessor<Time, Message> messageByTime() {
    if (ifMessageUpdated) updateMessages();
    return messageByTime;
  }

  public StoreAccessor<String, Message> messageByText() {
    if (ifMessageUpdated) updateMessages();
    return messageByText;
  }

  private void updateMessages(){

    messageById = new Store<>(UUID_COMPARE);
    messageByTime = new Store<>(TIME_COMPARE);
    messageByText = new Store<>(STRING_COMPARE);

    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
      Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(messageTable)));
      Scan scan = new Scan();

      System.out.println("Scan for all messages:");
      ResultScanner scanner = table.getScanner(scan);
      //Uuid id, Uuid next, Uuid previous, Time creation, Uuid author, String content
      for (Result row : scanner) {
        Uuid id = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("id"))));
        Uuid next = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("next"))));
        Uuid previous = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("previous"))));
        Time creation = Time.fromMs(Bytes.toLong(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("creation"))));
        Uuid author = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("author"))));
        Uuid conversation = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("conversation"))));
        Uuid group = Uuid.fromUuidString(Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("group"))));
        String content = Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("content")));

        Message message = new Message(id, next, previous, creation, author, conversation, group, content);
        messageById.insert(message.id, message);
        messageByTime.insert(message.creation, message);
        // TODO: check here
        messageByText.insert(message.content, message);
      }

    } catch (IOException e) {
      System.err.println("Exception while running GetUsers in Model: " + e.getMessage());
      e.printStackTrace();
    }
    ifMessageUpdated = false;
  }

}
