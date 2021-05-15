package argreader;

import java.util.HashMap;
import java.util.Set;

public class ArgReader {

	private HashMap<String, ArgFunction> argMap;
	private HashMap<String, Boolean> requiredArgs;

	public ArgReader() {
		argMap = new HashMap<>();
		requiredArgs = new HashMap<>();
	}

	public void addArg(String flag, boolean required,  ArgFunction arg) {
		argMap.put(flag, arg);
		requiredArgs.put(flag, false);
	}

	public void parseArgs(String[] args) throws Exception {
	
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') {
				
				String arg = args[i].substring(1, args[i].length());
				if (argMap.containsKey(arg)) {
					String param = null;
					if (i + 1 < args.length && args[i + 1].charAt(0) != '-')
						param = args[++i];
					
					argMap.get(arg).parse(param);
					if (requiredArgs.containsKey(arg)) 
						requiredArgs.replace(arg, true);
				}

			}
		}

		Set<String> keys = requiredArgs.keySet();
		for (String key: keys) 
			if (!requiredArgs.get(key)) throw new Exception("Argument '" + key + "' required");

	}
	

}
