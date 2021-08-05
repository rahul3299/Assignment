import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.1"

project {

    vcsRoot(Github)

    buildType(DockerPull)
    buildType(Package_1)
    buildType(Display)
    buildType(MavenBuild)
    buildType(DockerPush)
    buildType(DockerRun)
}

object Display : BuildType({
    name = "Display"

    vcs {
        root(Github)
    }

    steps {
        script {
            scriptContent = """echo "Completed """"
        }
    }

    triggers {
        vcs {
            branchFilter = ""
        }
    }

    dependencies {
        snapshot(DockerRun) {
            reuseBuilds = ReuseBuilds.NO
        }
    }
})

object DockerPull : BuildType({
    name = "Docker_pull"

    vcs {
        root(Github)
    }

    steps {
        dockerCommand {
            commandType = build {
                source = file {
                    path = "Dockerfile"
                }
                namesAndTags = "rahul3299/teams:%build.number%"
                commandArgs = "--pull"
            }
        }
    }

    dependencies {
        snapshot(Package_1) {
            reuseBuilds = ReuseBuilds.NO
        }
    }
})

object DockerPush : BuildType({
    name = "docker_push"

    vcs {
        root(Github)
    }

    steps {
        dockerCommand {
            name = "docker push"
            commandType = push {
                namesAndTags = "rahul3299/teams:%build.number%"
            }
            param("dockerfile.path", "Dockerfile")
        }
    }

    dependencies {
        snapshot(DockerPull) {
            reuseBuilds = ReuseBuilds.NO
        }
    }
})

object DockerRun : BuildType({
    name = "docker_run"

    vcs {
        root(Github)
    }

    steps {
        script {
            name = "Docker run"
            scriptContent = """
                docker stop Rahul
                docker rm -f Rahul
                docker run --name Rahul -d -p 5030:8080 rahul3299/teams:%build.number%
            """.trimIndent()
        }
    }

    dependencies {
        snapshot(DockerPush) {
            reuseBuilds = ReuseBuilds.NO
        }
    }
})

object MavenBuild : BuildType({
    name = "Maven_build"

    vcs {
        root(Github)
    }

    steps {
        maven {
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }
})

object Package_1 : BuildType({
    id("Package")
    name = "Package"

    vcs {
        root(Github)
    }

    steps {
        maven {
            goals = "package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    dependencies {
        snapshot(MavenBuild) {
            reuseBuilds = ReuseBuilds.NO
        }
    }
})

object Github : GitVcsRoot({
    name = "github"
    url = "https://github.com/rahul3299/Assignment.git"
    branch = "master"
})

