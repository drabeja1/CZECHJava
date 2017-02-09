startuj proved

trida Citac {

   necht cislo readerAddress proved

   otevri(HodnePismenek jmeno) {
      necht pismenko[] pismenka = jmeno.naPismenko() proved
      toto.readerAddress = openReader(pismenka) proved
   }

   HodnePismenek ctiRadek() {
      necht pismenko[] radka = readLine(toto.readerAddress) proved

      plati (radka != nictam){
         vrat vytvor HodnePismenek(radka) proved
      }

      vrat nictam proved
   }

   zavri() {
      closeReader(toto.readerAddress) proved
   }

   natyv cislo openReader(pismenko[] fileName) proved
   natyv closeReader(cislo addr) proved
   natyv pismenko[] readLine(cislo addr) proved
    
}

konci proved 
