package net.epsilony.gradle

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class CommonJava implements Plugin<Project>{

	private static final String DEFAULT_REPOSITORY = 'https://epsilony.net/nexus/content/groups/public'
	private static final String DEFAULT_DEPLOY_RELEASES_URL='https://epsilony.net/nexus/content/repositories/releases'
	private static final String DEFAULT_DEPLOY_SNAPSHOTS_URL='https://epsilony.net/nexus/content/repositories/snapshots'

	@Override
	public void apply(Project target) {

		target.apply([plugin : 'java'])
		target.apply([plugin : 'eclipse-wtp'])
		target.apply([plugin : 'idea'])
		target.apply([plugin : 'maven'])

		if (!target.hasProperty('nexusPublicUrl')){
			target.ext.nexusPublicUrl = DEFAULT_REPOSITORY
		}

		target.repositories{
			jcenter()
			maven{ url target.nexusPublicUrl }
		}

		target.eclipse{
			classpath {
				downloadSources = true
				downloadJavadoc = true
			}
		}

		target.compileJava { options.encoding = 'UTF-8' }

		addTasks(target);


		target.uploadArchives {
			repositories {
				if (!target.hasProperty('nexusDeployReleasesUrl')){
					target.ext.nexusDeployReleasesUrl = DEFAULT_DEPLOY_RELEASES_URL
				}

				if (!target.hasProperty('nexusDeploySnapshotsUrl')){
					target.ext.nexusDeploySnapshotsUrl = DEFAULT_DEPLOY_SNAPSHOTS_URL
				}
				mavenDeployer {
					repository(url: project.nexusDeployReleasesUrl){
						if(project.hasProperty('nexusDeployUser')&&project.hasProperty('nexusDeployPassword')){
							authentication(userName: project.nexusDeployUser, password: project.nexusDeployPassword)
						}
					}
					snapshotRepository(url: project.nexusDeploySnapshotsUrl){
						if(project.hasProperty('nexusDeployUser')&&project.hasProperty('nexusDeployPassword')){
							authentication(userName: project.nexusDeployUser, password: project.nexusDeployPassword)
						}
					}
				}
			}
		}
	}

	void addTasks(target){
		target.task('showRepositories') << {
			project.repositories.each{ println "repository: ${it.name} ('${it.url}')" }
		}
	}
}
