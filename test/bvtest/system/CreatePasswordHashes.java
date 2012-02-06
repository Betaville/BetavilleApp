/**
 * 
 */
package bvtest.system;

import edu.poly.bxmc.betaville.util.Crypto;

/**
 * @author Skye Book
 *
 */
public class CreatePasswordHashes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length>0){
			String[] data = Crypto.createBetavilleHash(args[0]);
			System.out.println(data[0]);
			System.out.println(data[1]);
		}
		else{
			System.out.println("A password is needed to generate user hashes");
		}
	}

}
