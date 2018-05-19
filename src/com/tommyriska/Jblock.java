package com.tommyriska;

import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class Jblock {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 6;
	
	public static void main(String[] args) {
		blockchain.add(new Block("This is the first block in the chain", "0"));
		System.out.println("Trying to mine block 1...");
		blockchain.get(0).mineBlock(difficulty);
		
		blockchain.add(new Block("This is the second block in the chain", blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to mine block 2...");
		blockchain.get(1).mineBlock(difficulty);
		
		
		blockchain.add(new Block("This is the third block in the chain", blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to mine block 3...");
		blockchain.get(2).mineBlock(difficulty);
		
		System.out.println("\nBlockchain is valid: " + isChainValid());
		
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe blockchain: ");
		System.out.println(blockchainJson);
	}
	
	public static boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		
		// loop and check hashes
		for(int i = 1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
			if(!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("Current hashes not equal");
				return false;
			}
			if(!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("Previous hashes not equal");
				return false;
			}
		}
		return true;
	}

}
