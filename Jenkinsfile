def awsCredentials = [[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-personal']]

pipeline {
  agent any
  environment {
  AWS_REGION = 'us-east-2'
  HOME = '.'
}
  options {
  disableConcurrentBuilds()
  parallelsAlwaysFailFast()
  timestamps()
  withCredentials(awsCredentials)
}
  stages{
            
            stage('Build'){
                steps{
                
                    bat "mvn clean install"
                }
            }
            stage('Sonar Analysis'){
                steps{
                    withSonarQubeEnv('Sonarqube'){
                        bat 'mvn sonar:sonar'
                    }
                }
            }
            stage('Artifactory'){
                steps{
                    rtMavenDeployer(
                        id: 'deployer',
                        serverId: 'Artifactory',
                        releaseRepo: 'example-repo-local',
                        snapshotRepo: 'example-repo-local')
                    rtMavenRun(
                        pom:'pom.xml',
                        goals:'clean install',
                        deployerId:'deployer')
                    rtPublishBuildInfo(
                        serverId:'Artifactory')
                }
            }
             stage("Docker Image"){
            steps{
                bat "docker image build -t rahul3299/my-assignment:${BUILD_NUMBER} ."
            }
        }
        stage('Uploading Image')
           {
               steps
               {
                   bat "docker login -u rahul3299 -p rmsc03021999@"
                   bat "docker push rahul3299/my-assignment:${BUILD_NUMBER}"
               }
           }
    
    stage('Pull image from EC2')
    {
      steps
      { script
       
       {
        
         bat """ssh -i rahul.pem -t -o StrictHostKeyChecking=no ec2-user@18.222.169.223 "sudo docker rm --force my-assignment; sudo docker run -d --name my-assignment -p 9098:8080 rahul3299/my-assignment:${BUILD_NUMBER}" """
      }
      }
    }
    
           stage('Docker Run')
               {
                   steps
                   {
                       script{
                           bat "docker rm --force my-assignment"
                            bat "docker run -d --name my-assignment -p 9095:8080 rahul3299/my-assignment"
                       }
                      
                   }
               }
   
            
            
        }

}
