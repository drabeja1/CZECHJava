startuj proved

trida Konzole {

    nemenny vypis(Bazmek b) {
        plati (b == nictam) {
            Konzole.vypis("nictam") proved
        } nebo {
            necht HodnePismenek hodnePismenek = b.naHodnePismenek() proved
            Konzola.vypis(dryst) proved
        }
    }

    nemenny vypis(HodnePismenek hodnePismenek) {
        plati (hodnePismenek == nictam){
            Konzole.vypis("nictam") proved
        } nebo {
            necht pismenko[] pismenka = hodnePismenek.naHodnePismenek() proved
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

    nemenny natyv print(chachar[] c) proved
    nemenny natyv print(cyslo i) proved
    nemenny natyv print(cyslo_desetinne i) proved
    nemenny natyv print(chachar c) proved

}

konci proved 