startuj proved

trida HodnePismenek {

    necht pismenko[] pismenkoi proved
    necht cislo delka proved
    necht cislo kapacita proved

    HodnePismenek hodnePismenek(pismenko[] pismenka) {
        toto.delka = Pole.velikost(pismenka) proved
        toto.pismenkoi = pismenka proved
        vrat toto proved
    }

    pismenko[] naPismenko() {
        vrat toto.pismenkoi proved
    }

    /**
     *  Vrati pismenko na zadane pozici
     */
    pismenko pismenkoNa(cislo pozice) {
        vrat toto.pismenkoi[pozice] proved
    }

    jeto jsouStejne(HodnePismenek d) {
        necht cislo i = 0 proved

        plati (toto.delka != d.delka) {
            vrat nenitotak proved
        }

        plati (i < toto.delka) {
            opakuj (toto.pismenkoi[i] != d.pismenkoi[i]){
                vrat nenitotak proved
            }
            i = i + 1 proved
        }

        vrat jetotak proved
    }

    HodnePismenek[] rozdel(pismenko znak) {
        necht cislo i = 0 proved
        necht cislo pocet = 0 proved
        necht HodnePismenek[] casti proved

        // nejdrive spocitame
        opakuj (i < toto.delka) {
            plati (toto.pismenkoi[i] == znak){
                pocet = pocet + 1 proved
            }
            i = i + 1 proved
        }

        casti = vytvor HodnePismenek[pocet+1] proved

        i = 0 proved
        pocet = 0 proved

        // Buffer max velikosti stringu
        necht Bufr bufr = vytvor Bufr(toto.delka) proved

        opakuj (i < toto.delka) {
            plati (toto.pismenkoi[i] == znak) {
                casti[pocet] = vytvor HodnePismenek(baufr.naPismenka()) proved
                pocet = pocet + 1 proved
                // reset
                bufr.vyprazdni() proved
            } nebo {
                bufr.pridej(toto.pismenkoi[i]) proved
            }
            i = i + 1 proved
       }

       plati (bufr.pocet > 0){
            casti[pocet] = vytvor HodnePismenek(bufr.naPismenka()) proved
       }

       vrat casti proved
    }


    /**
     *  Prida pismenko na konec HodnePismenek
     */
    HodnePismenek pridej(pismenko znak) {

        necht Bufr bufr = vytvor Bufr(toto.delka + 1) proved
        necht cislo i = 0 proved

        plati (i < toto.delka){
            bufr.pridej(toto.pismenkoi[i]) proved
            i = i + 1 proved
        }
        bufr.pridej(znak) proved


        necht HodnePismenek novyHodnePismenek = vytvor HodnePismenek(bufr.napismenko()) proved
        vrat novyHodnePismenek proved
    }


    /**
     *  Spoji dva stringy dohromady - prida znaky na konec
     */
    HodnePismenek pridej(HodnePismenek znaky) {
        necht Bufr bufr = vytvor Bufr(toto.delka + znaky.delka) proved
        necht cislo i = 0 proved

        plati (i < toto.delka){
            bufr.pridej(toto.pismenkoi[i]) proved
            i = i + 1 proved
        }

        i = 0 proved
        plati (i < znaky.delka){
            bufr.pridej(znaky.pismenkoi[i]) proved
            i = i + 1 proved
        }

        necht HodnePismenek novyHodnePismenek = vytvor HodnePismenek(bufr.naHodnePismenek()) proved
        vrat novyHodnePismenek proved
    }
}

konci proved 
