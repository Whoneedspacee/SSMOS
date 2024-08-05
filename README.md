# SSMOS

SSM Open Source Recreation for community usage

Feel free to fork the code and edit at your leisure 

https://discord.gg/GSnKwyjyU8

# Dependencies (required)	

PandaSpigot [repo](https://github.com/hpfxd/PandaSpigot)

Spigot 1.8.9

# Development Env Setup

1. Download the Source Code from https://github.com/Whoneedspacee/SSMOS
2. Run your IDE (probably intellij) and open up the SSMOS project
3. Allow the build scripts to initialize the developer environment
4. Press "maven" and "reload all maven projects" button (alternatively right-click "SSMOS" in the maven sub-menu and press "generate sources and update folders")
5. Once this process is finished, the developer environment should be ready and all your libraries compiled and ready to go

# Compiling code & gameplay

1. Press the "maven" menu, expand the "lifecycle" tab and click the "install" button
2. Wait for the IDE to successfully compile your code, which should end in a "BUILD SUCCESS"
3*. If you want to quickly and easily test your changes, you can run the start.bat within the "ssmos-template-server", since the .jar automatically builds to the plugins folder
4*. Alternatively, under the "projects" menu, expand the "target" directory, and copy-paste the file titled "SSMOS-1.0.jar" into your desired plugins folder

note: The template server does not come with maps pre-installed, you will have to gather these yourself. For more information, look at the readme file within that directory
