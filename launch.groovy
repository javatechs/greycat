def devices = DeviceManager.listConnectedDevice()
println "# Devices: " + devices.size() + " Device names:"
for (int i = 0; i < devices.size(); i++) {
println "- " + devices.get(i)
}

def cat =ScriptingEngine.gitScriptRun(	"https://github.com/OperationSmallKat/SmallKat_V2.git", 
								"loadRobot.groovy",
                                        [
								"https://github.com/javatechs/greycat.git"
								,"MediumKat.xml"
								,"GameController_22"
								,"hidDevice"
								]);
println "Cat loaded, searching for game controller"
//println "*** LAUNCH SCRIPT EXIT ***"
//return
def gameController =null
try{
	 gameController = ScriptingEngine.gitScriptRun(
	            "https://gist.github.com/javatechs/6c2454ada6192111f4d1d2ff36b8d521.git", // git location of the library
	            "LoadGameController.groovy",	// file to load
	            ["GameController_22"]			// Parameters passed to the function
	            );
      println "Game controller instantiated"
}catch (Exception ex) {
	// Prints to stderr. Terminal inside bowler doesn't see it.
	ex.printStackTrace()
	// Print to BStudio Terminal
	println("\nGame controller instantiation exception: ${ex}")
//	println("1---------------------------------------")
//	println(ex.toString());
//	println("2---------------------------------------")
//	println(ex.getMessage());
//	println("3---------------------------------------")
	println("\n"+ex.getStackTrace());  
    
}
if(gameController==null){
	println "Null game controller. Exiting script"
	return 
}

///////////////////////////////////////////////
def launcherController =null
if (0) {
try{
	 launcherController = ScriptingEngine.gitScriptRun(
	            "https://gist.github.com/javatechs/69be95abdd1231fe128291a9b9e19c3a", // git location of the library
	            "LoadLauncher.groovy",	// file to load
	            ["Launcher_22"]			// Parameters passed to the function
	            );
      println "Launcher controller instantiated"
}catch (Exception ex) {
	// Prints to stderr. Terminal inside bowler doesn't see it.
	ex.printStackTrace()
	// Print to BStudio Terminal
//	println("\Launcher controller instantiation exception: ${ex}")
	println("\n"+ex.getStackTrace());  
    
}
}
println "Connected!!"
println "3"
sleep(1000)
println "2"
sleep(1000)
println "1"
sleep(1000)
println "GO!!"

//int [] data = 
gameController.getData() 
double toSeconds=0.01//100 ms for each increment
println "Starting controller loop..."
while (!Thread.interrupted() ){
	Thread.sleep((long)(toSeconds*1000))
	data = gameController.getData() 
	//println data
	double xdata = data[3]
	double rzdata = data[2]
	double rxdata = data[0]
	double rydata = data[1]
	/*
	if(xdata<0)
		xdata+=256
	if(rzdata<0)
		rzdata+=256
	if(rxdata<0)
		rxdata+=256
	if(rydata<0)7
		rydata+=256
		*/
	double scale = 0.15
	double displacement = 40*(scale*(xdata/255.0)-scale/2)
	double displacementY =-10*(scale*(rxdata/255.0)-scale/2)
	
	double rot =((scale*2.0*rzdata/255.0)-scale)*-2.5
	double rotx =((rxdata/255.0)-scale/2)*5
	double roty =((rydata/255.0)-scale/2)*-5
	if(Math.abs(displacement)<0.1 ){
		displacement=0
	}
	if( Math.abs(rot)<0.1){
		rot=0
	}
	try{
		if(Math.abs(displacement)>0.16 || Math.abs(rot)>0.16 ||Math.abs(displacementY)>0.16  ){
//			println "displacement "+displacement+" rot "+rot+" straif = "+displacementY
			
			
			TransformNR move = new TransformNR(displacement,displacementY,0,new RotationNR(rotx,rot,roty))
			cat.DriveArc(move, toSeconds);
		}
		if(Math.abs(rotx)>2 || Math.abs(roty)>2){
			//println "tilt "+rotx+" rot "+roty
			TransformNR move = new TransformNR(displacement,displacementY,0,new RotationNR(rotx,0,roty))
			//cat.getWalkingDriveEngine().pose(move)
		}

	}catch(Throwable t){
		
		BowlerStudio.printStackTrace(t)
		
	}
	
}
