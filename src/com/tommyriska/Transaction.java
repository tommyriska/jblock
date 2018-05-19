package com.tommyriska;

import java.util.ArrayList;
import java.security.*;

public class Transaction {
	
	public String transactionId;
	public PublicKey sender;
	public PublicKey reciepient;
	public float value;
	public byte[] signature;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0; 
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	// THis calculates the transaction hash(which will be used as its ID)
	private String calculateHash() {
		sequence++;
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Float.toString(value) +
				sequence
				);
	}
	
	// Signs all the data we dont wish to be tampered with.
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}
	
	// Verifies the data we signed hasnt been tampered with
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
	
	// Returns true if new transaction could be created
	public boolean processTransaction() {
		
		if(verifySignature() == false) {
			System.out.println("#Transaction signature failed to verify");
			return false;
		}
		
		for(TransactionInput i : inputs) {
			i.UTXO = Jblock.UTXOs.get(i.transactionOutputId);
		}
		
		if(getInputsValue() < Jblock.minimumTransaction) {
			System.out.println("#Transaction inputs to small: " + getInputsValue());
			System.out.println("Please enter the amount greater than " + Jblock.minimumTransaction);
			return false;
		}
		
		float leftOver = getInputsValue() - value;
		transactionId = calculateHash();
		outputs.add(new TransactionOutput(this.reciepient, value, transactionId));
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));
		
		// add outputs to unspent list
		for(TransactionOutput o : outputs) {
			Jblock.UTXOs.put(o.id, o);
		}
		
		// remove transaction inputs from UTXO lists as spent
		for(TransactionInput i : inputs) {
			// If transaction can't be found skip it
			if(i.UTXO == null) continue;
			Jblock.UTXOs.remove(i.UTXO.id);
		}
		
		return true;			
	}
	
	// returns sum of inputs (UTXOs) values
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			// Skip if transaction is not found
			if(i.UTXO == null) continue;
			total += i.UTXO.value;
		}
		return total;
	}
	
	// returns sum of outputs
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
	
	
	
	
	
	
}	
