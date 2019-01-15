package com.songoda.kingdoms.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class LoreOrganizer {
	private static final int MAX = 40;

	public static List<String> organize(List<String> lores){
		List<String> newLore = new ArrayList<String>();

		String color = ChatColor.COLOR_CHAR + "f";
		String str = "";
		for(String lore : lores){
			if((lore).length() <= MAX){
				newLore.add(lore);
				continue;
			}
			String[] sentences = ChatColor.stripColor(lore).split(" ");
			if(lore.startsWith(ChatColor.COLOR_CHAR + "")){
				color = lore.substring(0, 2);
			}

			int index = 0;
			for(String sentence : sentences){

				if((str + " " +sentence).length() <= MAX){
					str += index == 0 ? sentence : " "+sentence;
				}else{
					newLore.add(color+str);
					str = sentence;
				}
				index++;
			}

			newLore.add(color+str);
			str = "";
		}

		return newLore;
	}
}


///////////////////////////////////////////////////////////////////
//	 public static ArrayList<String> addLore(String line, ArrayList<String> lore)
//	  {
//	    ArrayList thelore = (ArrayList)lore.clone();
//	    String[] split = split(line, 35);
//	    for (String part : split)
//	    {
////	      if (part.contains("-")) {
////	        String[] splitpart = part.split("-");
////	        for (int j = 0; j < splitpart.length; j++)
////	          thelore.add(splitpart[j]);
////	      }
////	      else {
//	        thelore.add(ChatColor.GREEN + part);
////	      //}
//	    }
//
//	    return thelore;
//	  }
//
//	  public static String[] split(String s, int length)
//	  {
//	    if (s.length() <= length) return new String[] { s };
//	    String[] split = new String[s.length() / length + 1];
//	    Arrays.fill(split, "");
//	    StringBuilder sb = new StringBuilder();
//	    boolean nextLine = false;
//	    char[] c = s.toCharArray();
//	    int index = 0;
//	    for (int i = 0; i < c.length; i++) {
//	      if ((i != 0) && (i % length == 0)) {
//	        nextLine = true;
//	      }
//	      if ((nextLine) && ((c[i] == ' ') || (c[i] == '|'))) {
//	        nextLine = false;
//	        split[(index++)] = sb.toString();
//	        sb = new StringBuilder();
//	      }
//	      else {
//	        sb.append(c[i]);
//	      }
//	    }
//	    split[index] = sb.toString();
//
//	    return split;
//	  }
//
//		public static String colorize(String msg){
//			String coloredMsg = "";
//			for(int i = 0; i < msg.length(); i++)
//			{
//				if(msg.charAt(i) == '&')
//					coloredMsg += 'Â§';
//				else
//					coloredMsg += msg.charAt(i);
//			}
//			return coloredMsg;
//		}
//
//
//}
//
//		List<String> newLore = new ArrayList<String>();
//		List<String> newnewLore = new ArrayList<String>();
//		String color = "&a";
//		boolean next = false;
//		for(String lore: lores){
//			if(lore.contains("&")){
//				for(String part:lore.split("")){
//					if(part.equals("&")){
//						next = true;
//					}
//					if(next){
//						next = false;
//						color = "&" + part;
//					}
//					newLore.add(lore);
//
//				}
//
//			}else{
//				newLore.add(color + lore);
//			}
//		}
//		String str = "";
//		int index = 0;
//		for(String lore:newLore){
//			String[] sentences = lore.split(" ");
//			for(String sentence : sentences){
//				if((str + " " +sentence).length() <= MAX){
//					str += index == 0 ? sentence : " "+sentence;
//				}else{
//					newnewLore.add(colorize(color+str));
//					str = sentence;
//				}
//				index++;
//			}
//
//			newnewLore.add(colorize(color+str));
//			str = "";
//		}
//
//		return newnewLore;
//	}
//
//	public static String colorize(String msg){
//        String coloredMsg = "";
//        for(int i = 0; i < msg.length(); i++)
//        {
//            if(msg.charAt(i) == '&')
//                coloredMsg += 'Â§';
//            else
//                coloredMsg += msg.charAt(i);
//        }
//        return coloredMsg;
//    }
//}
		
