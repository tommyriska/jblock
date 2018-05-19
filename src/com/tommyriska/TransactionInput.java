package com.tommyriska;

public class TransactionInput {
	public String transactionOutputId;
	public TransactionOutput UTXO; // Contains the unspent transaction output
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
