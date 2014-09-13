package tool;

public class charDeal {
	
	public static String subComma(String input) {
		String result = "";
		char[] inputList = input.toCharArray();
		for (int i = 0; i < inputList.length; i++) {
			if (!String.valueOf(inputList[i]).equals(",")) {
				result += inputList[i];
			}
		}
		return result;
	}
	
	public static Boolean hasDigital(String input) {
		Boolean result = false;
		for (char ele : input.toCharArray()) {
			result = Character.isDigit(ele);
			if (result == true){
				break;
			}
		}
		return result;
	}
	
	public static Boolean ifhasChar(String input) {
		Boolean result = false;
		for (char ele : input.toCharArray()) {
			String.valueOf(ele).equals(input);
			if (result == true){
				break;
			}
		}
		return result;
	}
	
	public static String extractDigital(String input) {
		String result = "";
		for (char ele : input.toCharArray()) {
			if( Character.isDigit(ele)){
				result += String.valueOf(ele);
			}
		}
		return result;
	}

}
