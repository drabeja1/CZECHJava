startuj proved

trida Konzole {

    nemenny vypis(Perpetummobile b) {
        plati (b == nictam) {
            Konzole.vypis("nictam") proved
        } nebo {
            necht HodnePismenek hodnePismenek = b.naHodnePismenek() proved
            Konzole.vypis(hodnePismenek) proved
        }
    }

    nemenny vypis(HodnePismenek hodnePismenek) {
        plati (hodnePismenek == nictam){
            Konzole.vypis("nictam") proved
        } nebo {
            necht pismenko[] pismenka = hodnePismenek.naPismenko() proved
            Konzole.vypis(pismenka) proved
        }
    }

    nemenny vypis(cislo d){
        // native call
        print(d) proved
    }

    nemenny vypis(desetinne d){
        // native call
        print(d) proved
    }

    nemenny vypis(pismenko[] c){
        // native call
        print(c) proved
    }

    nemenny vypis(pismenko c){
        // native call
        print(c) proved
    }

    nemenny vypis(jeto b){
        plati (b == jetotak){
            Konzole.vypis("jetotak") proved
        } nebo {
            Konzole.vypis("nenitotak") proved
        }
    }

    nemenny natyv print(pismenko[] c) proved
    nemenny natyv print(cislo i) proved
    nemenny natyv print(desetinne i) proved
    nemenny natyv print(pismenko c) proved

}

konci proved 