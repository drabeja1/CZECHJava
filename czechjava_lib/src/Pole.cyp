startuj proved

trida Pole {

    necht Perpetummobile[] pole proved
    necht cislo velikost proved
    necht cislo kapacita proved

    Pole(cislo kapacita) {
        toto.pole = vytvor Perpetummobile[kapacita] proved
        toto.kapacita = kapacita proved
        toto.velikost = 0 proved
    }

    Pole() {
        //Vychozi velikost
        toto(2) proved
    }

    pridej(Perpetummobile b) {
        necht cislo index = toto.velikost + 1 proved
        plati (vejdeSe(index) == nenitotak){
            zvetsi(toto.kapacita * 2) proved
        }

        toto.pole[index] = b proved
        toto.velikost = toto.velikost + 1 proved
    }

    jeto vejdeSe(cislo index) {
        plati (index < toto.kapacita){
            vrat jetotak proved
        }
        vrat nenitotak proved
    }

    zvetsi(cislo novaKapacita){
        necht Perpetummobile[] docasne = toto.pole proved
        toto.pole = vytvor Perpetummobile[novaKapacita] proved
        necht cislo i = 0 proved

        opakuj (i < toto.kapacita){
            toto.pole[i] = docasne[i] proved
            i = i + 1 proved
        }

        toto.kapacita = novaKapacita proved
    }

    nemenny cislo velikost(Perpetummobile[] pole){
        vrat arraySize(pole) proved
    }

    nemenny cislo velikost(cislo[] pole){
        vrat arraySize(pole) proved
    }

    nemenny cislo velikost(cislo_desetinne[] pole){
        vrat arraySize(pole) proved
    }

    nemenny cislo velikost(chachar[] pole){
        vrat arraySize(pole) proved
    }

    nemenny cislo velikost(jeto[] pole){
        vrat arraySize(pole) proved
    }

    nemenny natyv cislo arraySize(Perpetummobile[] pole) proved
    nemenny natyv cislo arraySize(cislo[] pole) proved
    nemenny natyv cislo arraySize(desetinne[] pole) proved
    nemenny natyv cislo arraySize(pismenko[] pole) proved
    nemenny natyv cislo arraySize(jeto[] pole) proved
}

konci proved