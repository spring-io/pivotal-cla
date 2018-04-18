#!/bin/bash

APP_NAME=$1
STAGING_CLIENT_ID=$2
STAGING_CLIENT_SECRET=$3
TOKEN_SECRET=$4
INFO_VERSION=$5

##########################################################################################

# the name that the current production application will get temporarily while we deploy
# the new app to APP_NAME
VENERABLE_APP_NAME="$APP_NAME-venerable"

cf delete $VENERABLE_APP_NAME -f
cf rename $APP_NAME $VENERABLE_APP_NAME
cf push $APP_NAME --no-start -p build/libs/*.jar
cf set-env $APP_NAME SECURITY_OAUTH2_MAIN_CLIENT_ID $STAGING_CLIENT_ID
cf set-env $APP_NAME SECURITY_OAUTH2_MAIN_CLIENT_SECRET $STAGING_CLIENT_SECRET
cf set-env $APP_NAME SECURITY_OAUTH2_PIVOTAL-CLA_TOKENSECRET $TOKEN_SECRET
cf set-env $APP_NAME INFO_VERSION $INFO_VERSION
cf restage $APP_NAME

if cf start $APP_NAME ; then
  # the app started successfully so remove venerable app
  cf delete $VENERABLE_APP_NAME -f
else
  # the app failed to start so delete the newly deployed app and rename old app back
  cf delete $APP_NAME -f
  cf rename $VENERABLE_APP_NAME $APP_NAME
fi