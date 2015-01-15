package com.mobrembski.kmeviewer;


import java.math.BigDecimal;
import java.util.HashMap;

public class BitUtils {

    private static final HashMap tempMap = new HashMap<Integer,Integer>(){{
        //region ConversionMap
        put(70,1);
        put(71,1);
        put(72,2);
        put(73,2);
        put(74,2);
        put(75,3);
        put(76,3);
        put(77,3);
        put(77,3);
        put(78,4);
        put(79,4);
        put(80,5);
        put(81,5);
        put(82,5);
        put(83,6);
        put(84,6);
        put(85,7);
        put(86,7);
        put(87,7);
        put(88,8);
        put(89,8);
        put(90,9);
        put(91,9);
        put(92,9);
        put(93,10);
        put(94,10);
        put(95,10);
        put(96,11);
        put(97,11);
        put(98,12);
        put(99,12);
        put(100,12);
        put(101,13);
        put(102,13);
        put(103,14);
        put(104,14);
        put(105,14);
        put(106,15);
        put(107,15);
        put(108,15);
        put(109,16);
        put(110,16);
        put(111,17);
        put(112,17);
        put(113,17);
        put(114,18);
        put(115,18);
        put(116,19);
        put(117,19);
        put(118,19);
        put(119,20);
        put(120,20);
        put(121,21);
        put(122,21);
        put(123,21);
        put(124,22);
        put(125,22);
        put(126,22);
        put(127,23);
        put(128,23);
        put(129,24);
        put(130,24);
        put(131,24);
        put(132,25);
        put(133,25);
        put(134,26);
        put(135,26);
        put(136,26);
        put(137,27);
        put(138,27);
        put(139,27);
        put(140,28);
        put(141,28);
        put(142,29);
        put(143,29);
        put(144,29);
        put(145,30);
        put(146,30);
        put(147,31);
        put(148,31);
        put(149,31);
        put(150,32);
        put(151,32);
        put(152,33);
        put(153,33);
        put(154,33);
        put(155,34);
        put(156,34);
        put(157,34);
        put(158,35);
        put(159,35);
        put(160,36);
        put(161,36);
        put(162,36);
        put(163,37);
        put(164,37);
        put(165,38);
        put(166,38);
        put(167,38);
        put(168,39);
        put(169,39);
        put(170,40);
        put(171,40);
        put(172,40);
        put(173,41);
        put(174,41);
        put(175,41);
        put(176,42);
        put(176,42);
        put(177,42);
        put(178,43);
        put(179,43);
        put(180,43);
        put(181,44);
        put(182,44);
        put(183,45);
        put(184,45);
        put(185,45);
        put(186,46);
        put(187,46);
        put(188,46);
        put(189,47);
        put(190,47);
        put(191,48);
        put(192,48);
        put(193,48);
        put(194,49);
        put(195,49);
        put(196,50);
        put(197,50);
        put(198,50);
        put(199,51);
        put(200,51);
        put(201,52);
        put(202,54);
        put(202,54);
        put(203,55);
        put(204,56);
        put(205,57);
        put(206,58);
        put(207,59);
        put(208,60);
        put(209,61);
        put(210,62);
        put(211,63);
        put(212,64);
        put(213,65);
        put(214,66);
        put(215,67);
        put(216,68);
        put(217,69);
        put(218,70);
        put(219,71);
        put(220,73);
        put(221,74);
        put(222,75);
        put(223,76);
        put(224,77);
        put(225,78);
        put(226,79);
        put(227,80);
        put(228,81);
        put(229,82);
        put(230,83);
        put(231,84);
        put(232,85);
        put(233,86);
        put(234,87);
        put(235,88);
        put(236,89);
        put(237,91);
        put(238,92);
        put(239,93);
        put(240,94);
        put(241,95);
        put(242,96);
        put(243,97);
        put(244,98);
        put(245,99);
        put(246,100);
        put(247,101);
        put(248,102);
        put(249,103);
        put(250,104);
        put(251,105);
        put(252,106);
        put(253,107);
        put(254,108);
        put(255,110);
        //endregion
    }};

    public static boolean BitIsSet(int testbyte, int bitnum) {
        return (testbyte & bitnum) == bitnum;
    }

    public static int GetMaskedBytes(int testbyte, int mask) {
        return (testbyte & mask);
    }

    public static Float precision(int decimalPlace, Float d) {

        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static float GetVoltage(int rawVal) {
        return precision(2,rawVal*0.01955f);
    }

    public static int GetTemperature(int rawVal) {
        if(rawVal<70)
            return 0;
        return Integer.parseInt(tempMap.get(new Integer(rawVal)).toString());
    }
}


