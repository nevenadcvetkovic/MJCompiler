package rs.ac.bg.etf.pp1;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int x=2, y=3,z=4,a=1;
		x+=x+=z+=x+=3+4;
		System.out.println(x);
		System.out.println(y);
		System.out.println(z);
		/*
		 * probati da stavljam na neki stek naredbe + - * i kad krenem
		 * ok moze da se izgleda samo redom s desna na levo racuna
		 * postavim sve brojeve po redu
		 * upamtim naredbe + * isl na stek
		 * onda krenem tipa nekako na kraju izraza ***skontaj kako kraj***
		 * i upamtim naredbe za cuvanje u design
		 * i onda uzmem i od pozadi krenem pa stavljam prvo operaciju pa dup pa store
		 * dok ne potrosim oba steka
		 * znati da kombinacija moze samo na kraju da se stavi
		 * i nadati se da nece da budu debili da kombinuju operatore de ne treba
		 *  
		 * za niz treba proveriti kako kad se nadju u sred izraza da im namestim da 
		 * im struct cvor ima elem ne null
		 * inace bez += ne pravi problem tu jer samo ucita vrednost, ne treba nista da se pamti
		 * mozda samo struct iskoristiti
		 * 
		 * deluje da je sredjen struct problem
		 * sutra testiraj detaljno sa nizom na svim
		 * probaj i sa iteratorom
		 * 
		 * izgleda da se poslednji sa 2 mesta stavi na stek
		 * */

	}

}
