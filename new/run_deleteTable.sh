PROJECTID="codeuproject12345"
INSTANCEID="codeuinstance12345"
USERTABLE="user"
CONVERSATIONTABLE="conversation"
GROUPTABLE="group"
MESSAGETABLE="message"

mvn exec:java@delete \
    "-DprojectId=$PROJECTID" \
    "-DinstanceId=$INSTANCEID" \
    "-DuserTable=$USERTABLE" \
    "-DconversationTable=$CONVERSATIONTABLE" \
    "-DgroupTable=$GROUPTABLE" \
    "-DmessageTable=$MESSAGETABLE"