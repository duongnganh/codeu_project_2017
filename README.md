
# TRAVEL CHAT
[![Java 7](https://img.shields.io/badge/Java-7-blue.svg)](https://img.shields.io/badge/Java-7-blue.svg)  [![JavaFX 7](https://img.shields.io/badge/JavaFX-7-blue.svg)](https://img.shields.io/badge/JavaFX-7-blue.svg)  [![Google Cloud Bigtable](https://img.shields.io/badge/GoogleCloudBigtable-0.9.5.1-blue.svg)](https://img.shields.io/badge/GoogleCloudBigtable-0.9.5.1-blue.svg)  [![Maven](https://img.shields.io/badge/Maven-3.5.0-orange.svg)](https://img.shields.io/badge/Maven-3.5.0-orange.svg)  [![JFoenix](https://img.shields.io/badge/JFoenix-1.4.0-42ebf4.svg)](https://img.shields.io/badge/JFoenix-1.4.0-42ebf4.svg)

Travel Chat is a chat application designed for travellers to connect and explore the cities they are visiting. This could include communicating with locals and sharing tips, and meeting up with other tourists at their favorite locations. Users can join cities and topics of conversation within them to chat other users. Travel Chat is built using Java 7, JavaFX 7 and it implements Google's Material UI design. It uses Google Cloud Bigtable to achieve data persistence.

## DISCLAIMER

CODEU is a program created by Google to develop the skills of future software
engineers. This project is not an offical Google Product. This project is a
playground for those looking to develop their coding and software engineering
skills.


## ENVIRONMENT

All instructions here are relative to a LINUX environment. There will be some
differences if you are working on a non-LINUX system. We will not support any
other development environment.

This project was built using JAVA 7. It is recommended that you install
JAVA&nbsp;7 when working with this project.

This project uses the native HBase API to connect to and interact with Cloud Bigtable. Visit https://cloud.google.com/bigtable for more information.

**Table of Contents**

- [Downloading the code](#downloading-the-code)
- [Finding your way around the project](#finding-your-way-around-the-project)
- [Source Directories](#Source-Directories)
- [Costs](#costs)
- [Before you begin](#before-you-begin)
  - [Installing Maven](#installing-maven)
  - [Creating a Project in the Google Cloud Platform Console](#creating-a-project-in-the-google-cloud-platform-console)
  - [Enabling billing for your project.](#enabling-billing-for-your-project)
  - [Install the Google Cloud SDK.](#install-the-google-cloud-sdk)
  - [Setting Google Application Default Credentials](#setting-google-application-default-credentials)
- [Provisioning an instance](#provisioning-an-instance)
- [Running the application](#running-the-application)
- [Cleaning up](#cleaning-up)



## Downloading the code

1.  Clone the [FIX HERE repository][github-repo], to your local
    machine:

        https://github.com/neilbarooah/codeu_project_2017

2.  Navigate to the Travel-Chat directory.

        cd codeu_project_2017/Travel-Chat

[github-repo]: https://github.com/neilbarooah/codeu_project_2017


## Google Cloud Bigtable

### Costs

This sample uses billable components of Cloud Platform, including:

+   Google Cloud Bigtable

Use the [Pricing Calculator][bigtable-pricing] to generate a cost estimate
based on your projected usage.  New Cloud Platform users might be eligible for
a [free trial][free-trial].

[bigtable-pricing]: https://cloud.google.com/products/calculator/#id=1eb47664-13a2-4be1-9d16-6722902a7572
[free-trial]: https://cloud.google.com/free-trial


## Before you begin

This sample assumes you have [Java 8][java8] installed.

[java8]: http://www.oracle.com/technetwork/java/javase/downloads/

### Installing Maven

These samples use the [Apache Maven][maven] build system. Before getting
started, be sure to [download][maven-download] and [install][maven-install] it.
When you use Maven as described here, it will automatically download the needed
client libraries.

[maven]: https://maven.apache.org
[maven-download]: https://maven.apache.org/download.cgi
[maven-install]: https://maven.apache.org/install.html

### Creating a Project in the Google Cloud Platform Console

If you haven't already created a project, create one now. Projects enable you to
manage all Google Cloud Platform resources for your app, including deployment,
access control, billing, and services.

1. Open the [Cloud Platform Console][cloud-console].
1. In the drop-down menu at the top, select **Create a project**.
1. Give your project a name.
1. Make a note of the project ID, which might be different from the project
   name. The project ID is used in commands and in configurations.

[cloud-console]: https://console.cloud.google.com/

### Enabling billing for your project.

If you haven't already enabled billing for your project, [enable
billing][enable-billing] now.  Enabling billing allows is required to use Cloud Bigtable
and to create VM instances.

[enable-billing]: https://console.cloud.google.com/project/_/settings

### Install the Google Cloud SDK.

If you haven't already installed the Google Cloud SDK, [install the Google
Cloud SDK][cloud-sdk] now. The SDK contains tools and libraries that enable you
to create and manage resources on Google Cloud Platform.

[cloud-sdk]: https://cloud.google.com/sdk/

### Setting Google Application Default Credentials

Set your [Google Application Default
Credentials][application-default-credentials] by [initializing the Google Cloud
SDK][cloud-sdk-init] with the command:

    gcloud init

Or with the [application-default login](https://cloud.google.com/sdk/gcloud/reference/auth/application-default/login) command:

    gcloud auth application-default login

[cloud-sdk-init]: https://cloud.google.com/sdk/docs/initializing
[application-default-credentials]: https://developers.google.com/identity/protocols/application-default-credentials

If you get Application Default Credentials are not available, include the following in the ~/.bash_profile:
    export GOOGLE_APPLICATION_CREDENTIALS=/Users/YOUR-LAPTOP/.config/gcloud/application_default_credentials.json


## Provisioning an instance

Follow the instructions in the [user
documentation](https://cloud.google.com/bigtable/docs/creating-instance) to
create a Google Cloud Platform project and Cloud Bigtable instance if necessary.
You'll need to reference your project id and instance id to run the
application.



## Running the application

Place your projectId and instanceId in the following files:
  codeu_project_2017/Travel-Chat/IDs
  codeu_project_2017/Travel-Chat/src/main/java/codeu/chat/common/IDs.java

Build and run the sample using Maven.


  1. To build and test the project:
       ```
       $ sh build.sh
       ```
       
     Although there is a script to clean (clean.sh), please don't use it for testing purposes as it gets rid of all the media      files being used for the project.

  2. If the tables are not yet created, creat tables. This is done once.
       $ sh run_createTable.sh
       $ sh run_createTableForMainUI.sh

     To delete the created tables:
       $ sh run_deleteTable.sh

  3. To run the project you will need to run both the client and the server. Run
     the following two commands in separate shells:

       ```
       $ sh run_server.sh <team_id> <team_secret> <port> <persistent-dir>
       $ sh run_better_gui_client.sh <host> <port>
       ```
       
       For testing purposes, use these:
       
       ```
       $ sh run_server.sh 0 0 2000 bin
       $ sh run_client.sh 0 2000
       ```

       Although it is recommended that you use our GUI as posted above, you can also access the simple GUI provided by CodeU:
       
       ```
       $ sh run_server.sh <team_id> <team_secret> <port> <persistent-dir>
       $ sh run_simple_gui_client.sh <host> <port>
       ```


     You must specify the following startup arguments for `run_server.sh:
     + `<team_id>` and `<team_secret>`: a numeric id for your team, and a secret
       code, which are used to authenticate your server with the Relay server.
       You can specify any integer value for `<team_id>`, and a value expressed
       in hexadecimal format (using numbers `0-9` and letters in the range
       `A-F`) for `<team_secret>` when you launch the server in your local setup
       since it will not connect to the Relay server.
     + `<port>`: the TCP port that your Server will listen on for connections
       from the Client. You can use any value between 1024 and 65535, as long as
       there is no other service currently listening on that port in your
       system. The server will return an error:

         ```
         java.net.BindException: Address already in use (Bind failed)
         ```

       if the port is already in use.
     + `<persistent-dir>`: the path where you want the server to save data between
       runs.

     The startup arguments for `run_client.sh` are the following:
     + `<host>`: the hostname or IP address of the computer on which the server
       is listening. If you are running server and client on the same computer,
       you can use `localhost` here.
     + `<port>`: the port on which your server is listening. Must be the same
       port number you have specified when you launched `run_server.sh`.

All running images write informational and exceptional events to log files.
The default setting for log messages is "INFO". You may change this to get
more or fewer messages, and you are encouraged to add more LOG statements
to the code. The logging is implemented in `codeu.chat.util.Logger.java`,
which is built on top of `java.util.logging.Logger`, which you can refer to
for more information.

In addition to your team's client and server, the project also includes a
Relay Server and a script that runs it (`run_relay.sh`).
This is not needed to get started with the project.



## Cleaning up

To avoid incurring extra charges to your Google Cloud Platform account, remove
the resources created for this sample.

1.  Go to the Clusters page in the [Cloud
    Console](https://console.cloud.google.com).

    [Go to the Clusters page](https://console.cloud.google.com/project/_/bigtable/clusters)

1.  Click the cluster name.

1.  Click **Delete**.

    ![Delete](https://cloud.google.com/bigtable/img/delete-quickstart-cluster.png)

1. Type the cluster ID, then click **Delete** to delete the cluster.


## Acknowledgments

* CodeU team at Google for giving us the opportunity to participate in the program. Special thanks to Andrew Smith from Google for his ideas and continued guidance.

# Release Notes

```
Version 1.0.0
```
```
New Features:
* Data Persistence and Google Cloud Bigtable integration
* Add groups/cities.
* Material UI implemented for GUI
* Delete user
* Delete conversation/topic
* Users have nicknames
* Handles duplicate usernames/topics
* Password-based sign-in

Known Bugs and Defects:
* Pressing the <enter> button on the message panel, login and registration forms crashes the app. It is
* recommended that you click on the "Send", "Register", and "Login" buttons provided in the UI.
* Certain features such as deleting users/conversations, manually adding conversations/groups is
* implemented for the command line only.
```

