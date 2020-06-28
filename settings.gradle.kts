rootProject.name = "ben93riggs Plugins"

include(":cannonreloader")
include(":eventdebugger")
include(":foodeater")
include(":itemcombiner")
include(":itemuser")
include(":jadautoprayer")
include(":pktools")
include(":praypotdrinker")
include(":specialattackuser")
include(":nmzhelper")

for (project in rootProject.children) {
    project.apply {
        projectDir = file(name)
        buildFileName = "${name.toLowerCase()}.gradle.kts"

        require(projectDir.isDirectory) { "Project '${project.path} must have a $projectDir directory" }
        require(buildFile.isFile) { "Project '${project.path} must have a $buildFile build script" }
    }
}