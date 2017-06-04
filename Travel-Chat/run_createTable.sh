source IDs
USERTABLE="user"
CONVERSATIONTABLE="conversation"
GROUPTABLE="group"
MESSAGETABLE="message"

mvn exec:java@create \
    "-DprojectId=$PROJECTID" \
    "-DinstanceId=$INSTANCEID" \
    "-DuserTable=$USERTABLE" \
    "-DconversationTable=$CONVERSATIONTABLE" \
    "-DgroupTable=$GROUPTABLE" \
    "-DmessageTable=$MESSAGETABLE"
