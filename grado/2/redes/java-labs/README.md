# java-labs: Creating the environment for Java development and Git - 2019-2020

## Netcat (nc)
- Windows OS
    - Download MobaXterm from [here] (https://download.mobatek.net/1242019111120613/MobaXterm_Portable_v12.4.zip) and extract the content. No installation is needed. Netcat is available if you click on "Start local terminal".

- Linux OS
    - Netcat should already be installed on your system.

## NetBeans IDE 8.2 for Java SE

- Download NetBeans from [here](https://netbeans.org/downloads/8.2/) and install it using default options.


## Git 

- Download Git from [here](https://git-scm.com/downloads) and install it using default options
  > Note that for instance in Ubuntu systems you could download and install it by simply executing the following:
  
```shell
    sudo apt-get install git
```

- Basic configuration
    - In Windows OS, the following commands should be executed inside git-bash (`$GIT_HOME/git-bash.exe`):
    
```shell
    git config --global user.email "your_email@udc.es"
    git config --global user.name "Your Name"
```

> The following line illustrates how to set Sublime as the Git default editor, but you can use any other editor installed in your OS (you can download Sublime Text editor from [here](https://www.sublimetext.com/3))
      
```shell
    >Windows OS
	git config --global core.editor "'C:\Program Files\Sublime Text 3\sublime_text.exe' -w"
	
	>Linux OS
	git config --global core.editor "subl -w"
```

- [Optional]  Autocompletion utility for Git in Linux OS systems:
    - Follow instructions from [https://github.com/bobthecow/git-flow-completion/wiki/Install-Bash-git-completion](https://github.com/bobthecow/git-flow-completion/wiki/Install-Bash-git-completion)

### Creation and configuration of SSH Keys

- From the git-bash interpreter in Windows OS systems
> Generate SSH keys in the default path ($HOME/.ssh) and with default names
      
```shell
    ssh-keygen -t rsa -b 4096 -C "your_email@udc.es"
```    
    
- Open the browser and navigate to [https://git.fic.udc.es/profile/keys](https://git.fic.udc.es/profile/keys)
- In the "Key" field, copy the public key, i.e, content of file `$HOME/.ssh/id_rsa.pub`
- In the "Title" field, specify a name for the key
- Click on the "Add key" button

- Try SSH connectivity against the Git server and add it to the list of known hosts
  > Answer "yes" to the question "Are you sure you want to continue connecting (yes/no)?"
   
```shell
    ssh -T git@git.fic.udc.es
```   

## Creating your project

### Create Git repository in FIC GitLab web site

- Open the browser and navigate to  https://git.fic.udc.es/users/&lt;user-login&gt;/projects, where &lt;user-login&gt; is your user login.

- Click on "+" (on the right of the search box, on the top of the we page) 
  - Click on "New project".
  - Specify "java-labs" as the name of the project.
  - Type of project must remain to "Private" (default value).
  
- <code><b>Click on the "Members" link.</b></code>
  - Click on the "Add member" tab, and select "telematica.redes" as the member to invite.
  - Choose "Master" as the role permission.
  - Do not fill the expiration date field. 
  - Click on "Add to project" button.


### Initializing your Git repository

- Download project template from  [moodle](https://moodle.udc.es/course/view.php?id=55380) (`java-labs.zip` file)

```shell
	unzip java-labs.zip
	cd java-labs
	git init
	git remote add origin git@git.fic.udc.es:<user-login>/java-labs.git
	git add .
	git commit
	git push –u origin master
```

 NOTE that &lt;user-login&gt; must be changed by your user login.

### Load the project in NetBeans

- Click on "File" > "New Project" menu option.
- Choose "Java Category" and "Java Project with Existent Sources" project type.
- Click on "Next" button.
- Set "java-labs" as "Project Name".
- Choose folder "java-labs" as "Project Folder".
- Click on "Next" button.
- Click on "Add Folder ..." to add the "src" folder to the "Source Package Folders" of the project.
- Click on "Finish" button.

### [Optional] Install a graphical client for Git

- Recommended option could be "GitKraken". You can download it from [here](https://www.gitkraken.com/download)
