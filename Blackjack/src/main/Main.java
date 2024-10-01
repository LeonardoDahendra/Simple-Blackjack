package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Main {
	Random rand = new Random();
	Scanner scan = new Scanner(System.in);
	ArrayList<String[]> userDatas = new ArrayList<>();
	static byte[] key;
	static SecretKeySpec secretKey;
	int curUser = -1;
	String[] cardType = {"\u2665", "\u2660", "\u2666", "\u2663"};
	String[] cardNum = {"Intentional Empty", "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
	
	public int MainMenu()
	{
		int mainMenu = 0;
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		System.out.println("=====================");
		System.out.printf("| %s   BlueJack    %s |\n", cardType[0], cardType[1]);
		System.out.printf("| %s   Card Game   %s |\n", cardType[2], cardType[3]);
		System.out.println("=====================");
		System.out.println("| 1. Login          |");
		System.out.println("| 2. Register       |");
		System.out.println("| 3. Exit           |");
		System.out.println("=====================");
		System.out.print("Choose[1 - 3] >> ");
		try {
			mainMenu = scan.nextInt();
			scan.nextLine();
		} catch (Exception e) {
			// TODO: handle exception
			scan.nextLine();
		}
		return mainMenu;
	}
	
	public int MainMenuSecond()
	{
		int mainMenu = 0;
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		System.out.println("=====================");
		System.out.printf("| Hello, %-11s|\n", userDatas.get(curUser)[0]);
		System.out.printf("| Point: %-11s|\n", userDatas.get(curUser)[2]);
		System.out.println("=====================");
		System.out.println("| 1. Play           |");
		System.out.println("| 2. Highscore      |");
		System.out.println("| 3. Save & Logout  |");
		System.out.println("=====================");
		System.out.print("Choose[1 - 3] >> ");
		try {
			mainMenu = scan.nextInt();
			scan.nextLine();
		} catch (Exception e) {
			// TODO: handle exception
			scan.nextLine();
		}
		return mainMenu;
	}
	
	public static void setKey(final String myKey) {
	    MessageDigest sha = null;
	    try {
	    	key = myKey.getBytes("UTF-8");
	    	sha = MessageDigest.getInstance("SHA-1");
	    	key = sha.digest(key);
	    	key = Arrays.copyOf(key, 16);
	    	secretKey = new SecretKeySpec(key, "AES");
	    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
	    	e.printStackTrace();
	    }
	}
	
	public void saveGame()
	{ 
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("SuperS3cr3tFile.dat"));
			for (int i = 0; i < userDatas.size(); i++) {
				String combinedData = userDatas.get(i)[0] + "#" + userDatas.get(i)[1] + "#" + userDatas.get(i)[2];
				Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			    writer.write(Base64.getEncoder().encodeToString(cipher.doFinal(combinedData.getBytes("UTF-8"))));
			    writer.write("\n");
			}
			writer.close();	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void loadGame()
	{
		userDatas.clear();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("SuperS3cr3tFile.dat"));
			String line;
			while ((line = reader.readLine()) != null) {
				Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			    cipher.init(Cipher.DECRYPT_MODE, secretKey);
			    final String finaled = line;
			    line = new String(cipher.doFinal(Base64.getDecoder().decode(finaled)));
			    String[] temp = line.split("#");
				userDatas.add(temp);
			}	
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void GetRandomCard(ArrayList<ArrayList<Integer>> cards, int index)
	{
		int randCard = -1;
		int randType = -1;
		boolean canEnd = false;
		while (!canEnd) {
			randCard = rand.nextInt(13) + 1;
			int similarCard = 0;
			ArrayList<Integer[]> simCards = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < cards.get(i).size(); j++) {
					if (randCard == cards.get(i).get(j)) {
						simCards.add(new Integer[2]);
						simCards.get(similarCard)[0] = i;
						simCards.get(similarCard)[1] = j;
						similarCard++;
					}
				}
			}
			if (similarCard < 4) {
				while (!canEnd) {
					canEnd = true;
					randType = rand.nextInt(4);
					for (int j = 0; j < simCards.size(); j++) {
						if (cards.get(simCards.get(j)[0] + 2).get(simCards.get(j)[1]) == randType) {
							canEnd = false;
						}
					}
				}
			}
		}
		cards.get(index).add(randCard);
		cards.get(index + 2).add(randType);
	}
	
	public int Play(int bet)
	{
		int ret = 0;
		boolean finished = false;
		ArrayList<ArrayList<Integer>> cards = new ArrayList<>();
		int curCards = 2;
		for (int i = 0; i < 4; i++) {
			cards.add(new ArrayList<>());
		}
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				GetRandomCard(cards, i);
			}
		}
		int sum = 0;
		do {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
			System.out.println("Dealer Card:");
			System.out.printf("%s%s | ??\n", cardNum[cards.get(0).get(0)], cardType[cards.get(2).get(0)]);
			System.out.println("Player Card:");
			for (int i = 0; i < cards.get(1).size(); i++) {
				if (i != cards.get(1).size() - 1) {
					System.out.printf("%s%s | ", cardNum[cards.get(1).get(i)], cardType[cards.get(3).get(i)]);
				}
				else {
					System.out.printf("%s%s\n", cardNum[cards.get(1).get(i)], cardType[cards.get(3).get(i)]);
				}
			}
			System.out.println("====================");
			System.out.println("| Choose your move |");
			System.out.println("====================");
			System.out.println("| 1. Hit           |");
			System.out.println("| 2. Stand         |");
			System.out.println("====================");
			int input = 0;
			do {
				System.out.print("Choose[1 - 2] >> ");
				try {
					input = scan.nextInt();
					scan.nextLine();
				} catch (Exception e) {
					// TODO: handle exception
					scan.nextLine();
				}
			} while (input < 1 || input > 2);
			switch (input) {
			case 1:
				GetRandomCard(cards, 1);
				curCards++;
				break;
			case 2:
				finished = true;
				break;
			}
			sum = 0;
			for (int i = 0; i < curCards; i++) {
				if (cards.get(1).get(i) == 1) sum += 11;
				else if (cards.get(1).get(i) > 10) sum += 10;
				else sum += cards.get(1).get(i);
			}
			if (sum > 21) finished = true;
		} while (!finished);
		int dealerCard = 2;
		int dealerSum = 0;
		for (int i = 0; i < 2; i++) {
			if (cards.get(0).get(i) == 1) dealerSum += 11;
			else if (cards.get(0).get(i) > 10) dealerSum += 10;
			else dealerSum += cards.get(0).get(i);
		}
		while (dealerSum < 17) {
			GetRandomCard(cards, 0);
			if (cards.get(0).get(dealerCard) == 1) dealerSum += 11;
			else if (cards.get(0).get(dealerCard) > 10) dealerSum += 10;
			else dealerSum += cards.get(0).get(dealerCard);
			dealerCard++;
		}
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		System.out.println("Dealer Card:");
		for (int i = 0; i < dealerCard; i++) {
			if (i != dealerCard - 1) {
				System.out.printf("%s%s | ", cardNum[cards.get(0).get(i)], cardType[cards.get(2).get(i)]);
			}
			else {
				System.out.printf("%s%s\n", cardNum[cards.get(0).get(i)], cardType[cards.get(2).get(i)]);
			}
		}
		System.out.println("Player Card:");
		for (int i = 0; i < cards.get(1).size(); i++) {
			if (i != cards.get(1).size() - 1) {
				System.out.printf("%s%s | ", cardNum[cards.get(1).get(i)], cardType[cards.get(3).get(i)]);
			}
			else {
				System.out.printf("%s%s\n", cardNum[cards.get(1).get(i)], cardType[cards.get(3).get(i)]);
			}
		}
		System.out.println("====================================");
		if (sum > 21 && dealerSum > 21) {
			System.out.println("[!] Both of you are busted, you got nothing");
			ret = 2;
		}
		else if (dealerSum > 21 || (sum > dealerSum && sum <= 21)) {
			System.out.printf("[!] The dealer busted, you won %d point(s)\n", bet * 2);
			ret = 1;
		}
		else if (sum > 21 || (dealerSum > sum && dealerSum <= 21)) {
			System.out.printf("[!] %s busted, you lost %d point(s)\n", userDatas.get(curUser)[0], bet);
		}
		else if (sum == dealerSum) {
			System.out.println("[!] It's tie, you got nothing");
			ret = 2;
		}
		System.out.println("Press enter to continue...");
		scan.nextLine();
		return ret;
	}
	
	public void Merge(int start, int mid, int end)
	{
		ArrayList<String[]> tempDatas = new ArrayList<>();
		ArrayList<Integer> tempScores = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			tempDatas.add(userDatas.get(i));
			tempScores.add(Integer.parseInt(userDatas.get(i)[2]));
		}
		int l = 0;
		int r = 0;
		while (l < mid - start + 1 && r < end - mid) {
			if (tempScores.get(l) > tempScores.get(r + mid - start + 1)) {
				userDatas.set(l + r + start, tempDatas.get(l));
				l++;
			}
			else {
				userDatas.set(l + r + start, tempDatas.get(r + mid - start + 1));
				r++;
			}
		}
		while (l < mid - start + 1) {
			userDatas.set(l + r + start, tempDatas.get(l));
			l++;
		}
		while (r < end - mid) {
			userDatas.set(l + r + start, tempDatas.get(r + mid - start + 1));
			r++;
		}
	}
	
	public void MergeSort(int start, int end)
	{
		if (start >= end) return;
		int mid = start + (end - start) / 2;
		MergeSort(start, mid);
		MergeSort(mid + 1, end);
		Merge(start, mid, end);
	}
	
	public void Highscore()
	{
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		System.out.println("===========================");
		System.out.println("|        HIGHSCORE        |");
		System.out.println("===========================");
		System.out.println("| Username   | Point      |");
		System.out.println("===========================");
		String curUserName = userDatas.get(curUser)[0];
		MergeSort(0, userDatas.size() - 1);
		for (int i = 0; i < userDatas.size(); i++) {
			if (userDatas.get(i)[0].equals(curUserName)) curUser = i;
			System.out.printf("| %-11s| %-11s|\n", userDatas.get(i)[0], userDatas.get(i)[2]);
		}
		System.out.println("===========================");
		System.out.println("Press enter to continue...");
		scan.nextLine();
	}
	
	public void Game()
	{
		int mainMenu = 0;
		do {
			mainMenu = MainMenuSecond();
			switch (mainMenu) {
			case 1:
				int score = Integer.parseInt(userDatas.get(curUser)[2]);
				if (score > 0) {
					int bet = 0;
					do {
						System.out.printf("Input your bet [max %d]: ", score);
						try {
							bet = scan.nextInt();
							scan.nextLine();
						} catch (Exception e) {
							// TODO: handle exception
							scan.nextLine();
						}
						if (bet < 1 || bet > score) System.out.printf("[!] Input must be between 1 and %d\n", score);
					} while (bet < 1 || bet > score);
					int win = Play(bet);
					if (win == 1) {
						score += bet * 2;
					}
					else if (win == 0){
						score -= bet;
					}
					userDatas.get(curUser)[2] = String.valueOf(score);
				}
				else {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("=====================================");
					System.out.println("|           !!! ERROR !!!           |");
					System.out.println("=====================================");
					System.out.println("| Your account has reached 0 point  |");
					System.out.println("| and has been banned by the system |");
					System.out.println("=====================================");
					System.out.println("Press enter to continue...");
					scan.nextLine();
				}
				break;
			case 2:
				Highscore();
				break;
			case 3:
				saveGame();
			}
		} while (mainMenu != 3);	
	}
	
	public void Login()
	{
		String username;
		String password;
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		System.out.print("Input username: ");
		username = scan.nextLine();
		System.out.print("Input password: ");
		password = scan.nextLine();
		for (int i = 0; i < userDatas.size(); i++) {
			if (userDatas.get(i)[0].equals(username) && userDatas.get(i)[1].equals(password)) {
				curUser = i;
				break;
			}
		}
		if (curUser == -1) System.out.println("[!] Invalid Username and Password");
		else System.out.println("[*] Successfully logged in");
		
		System.out.println("Press enter to continue...");
		scan.nextLine();
		if (curUser != -1) Game();
	}
	
	public void Register()
	{
		String userName;
		String password;
		boolean available = true;
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		do {
			available = true;
			System.out.print("Input username: ");
			userName = scan.nextLine();
			if (userName.length() < 4 || userName.length() > 10) System.out.println("[!] Username must be between 4 and 10 characters");
			for (int i = 0; i < userDatas.size(); i++) {
				if (userName.equals(userDatas.get(i)[0])) {
					available = false;
					System.out.println("[!] Username already exist");
				}
			}
		} while (userName.length() < 4 || userName.length() > 10 || !available);
		
		available = false;
		do {
			boolean hasLetter = false;
			boolean hasInt = false;
			System.out.print("Input password: ");
			password = scan.nextLine();
			if (password.length() < 8 || password.length() > 16) System.out.println("[!] Password must be between 8 and 16 characters");
			for (int i = 0; i < password.length(); i++) {
				if ((password.charAt(i) >= 65 && password.charAt(i) <= 90) || (password.charAt(i) >= 97 && password.charAt(i) <= 122)) {
					hasLetter = true;
				}
				else if(password.charAt(i) >= 48 && password.charAt(i) <= 57) {
					hasInt = true;
				}
			}
			if (!hasLetter || !hasInt) System.out.println("[!] Password must be alphanumeric");
			else available = true;
		} while (password.length() < 8 || password.length() > 16 || !available);
		String[] combined = new String[3];
		combined[0] = userName;
		combined[1] = password;
		combined[2] = "100";
		userDatas.add(combined);
		saveGame();
		System.out.println("[*] Successfully registered an account");
		System.out.println("Press enter to continue...");
		scan.nextLine();
	}

	public Main() {
		// TODO Auto-generated constructor stub
		setKey("Us3rS3CR3TD4T4SS");
		loadGame();
		int mainMenu = 0;
		do {
			curUser = -1;
			mainMenu = MainMenu();
			switch (mainMenu) {
			case 1:
				Login();
				break;
			case 2:
				Register();
				break;
			}
		} while (mainMenu != 3);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();
	}

}
