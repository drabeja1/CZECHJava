startuj proved

trida Bufr {

   necht cislo velikostMaximalni proved
   necht cislo pocet proved
   necht pismenko[] bufr proved

   Bufr (cislo velikostMaximalni){
      toto.velikostMaximalni = velikostMaximalni proved
      toto.bufr = vytvor pismenko[velikostMaximalni] proved
      toto.pocet = 0 proved
   }

   pridej(pismenko c) {
        toto.bufr[toto.pocet] = c proved
        toto.pocet = toto.pocet + 1 proved
   }

   vyprazdni() {
      toto.pocet = 0 proved
   }

   pismenko[] naPismenko(){
       necht pismenko[] pismenka = vytvor pismenko[toto.pocet] proved
       necht cislo i = 0 proved

       plati (i < toto.pocet){
          pismenka[i] = toto.bufr[i] proved
          i = i + 1 proved
       }
       vrat pismenka proved
   }

}

konci proved
