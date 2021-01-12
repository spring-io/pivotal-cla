def projectProperties = [
	[$class: 'BuildDiscarderProperty',
		strategy: [$class: 'LogRotator', numToKeepStr: '5']]
]
properties(projectProperties)

def SUCCESS = hudson.model.Result.SUCCESS.toString()
currentBuild.result = SUCCESS

try {
	build: {
		stage('Build') {
			node {
				checkout scm
				sh "./ci/scripts/start-services.sh"
				try {
					withEnv(["JAVA_HOME=${ tool 'jdk8' }"]) {
						sh "./gradlew clean assemble check --no-daemon --stacktrace"

						sh "./ci/scripts/install-cf.sh"
						withCredentials([usernamePassword(credentialsId: 'onecloud-svc_spring_builds', passwordVariable: 'CF_PASSWORD', usernameVariable: 'CF_USERNAME')]) {
							sh "./cf login -a api.sc2-04-pcf1-system.oc.vmware.com -o pivotal-cla -s prod -u '$CF_USERNAME' -p '$CF_PASSWORD'"
						}
						withCredentials([string(credentialsId: 'pivotal-cla-personal_access_token', variable: 'TOKEN_SECRET')]) {
							withCredentials([usernamePassword(credentialsId: 'pivotal-cla-client_id', passwordVariable: 'CLIENT_SECRET', usernameVariable: 'CLIENT_ID')]) {
								sh "./ci/scripts/cf-push.sh pivotal-cla $CLIENT_ID $CLIENT_SECRET $TOKEN_SECRET ${currentBuild.number}"
							}
						}
						sh "./cf logout"
					}
				} catch(Exception e) {
					currentBuild.result = 'FAILED: check'
					throw e
				} finally {
					junit '**/build/test-results/*/*.xml'
				}
			}
		}
	}
} catch(Exception e) {
	currentBuild.result = 'FAILED: deploys'
	throw e
} finally {
	def buildStatus = currentBuild.result
	def buildNotSuccess =  !SUCCESS.equals(buildStatus)
	def lastBuildNotSuccess = !SUCCESS.equals(currentBuild.previousBuild?.result)

	if(buildNotSuccess || lastBuildNotSuccess) {

		stage('Notifiy') {
			node {
				final def RECIPIENTS = [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']]

				def subject = "${buildStatus}: Build ${env.JOB_NAME} ${env.BUILD_NUMBER} status is now ${buildStatus}"
				def details = """The build status changed to ${buildStatus}. For details see ${env.BUILD_URL}"""

				emailext (
					subject: subject,
					body: details,
					recipientProviders: RECIPIENTS,
					to: "rwinch@pivotal.io"
				)
			}
		}
	}
}
