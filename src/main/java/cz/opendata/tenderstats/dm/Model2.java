package cz.opendata.tenderstats.dm;

public class Model2 implements Model {

    @Override
    public Double predictField9(String field1, String field2, String field3, String field4, String field5, String field6, String field7, Double field8) {
        if (field3 == null) {
            return 40082257.5486D;
        } else if (field3.equals("255")) {
            if (field4 == null) {
                return 2038889178.46D;
            } else if (field4.equals("0")) {
                if (field8 == null) {
                    return 20024800000D;
                } else if (field8 > 1080) {
                    return 37200000D;
                } else if (field8 <= 1080) {
                    return 60000000000D;
                }
            } else if (!field4.equals("0")) {
                if (field6 == null) {
                    return 540063276.667D;
                } else if (field6.equals("268")) {
                    return 5040000000D;
                } else if (!field6.equals("268")) {
                    if (field8 == null) {
                        return 275361116.471D;
                    } else if (field8 > 1140) {
                        if (field6.equals("0")) {
                            return 1200000000D;
                        } else if (!field6.equals("0")) {
                            if (field8 > 1800) {
                                return 690000000D;
                            } else if (field8 <= 1800) {
                                return 40207590D;
                            }
                        }
                    } else if (field8 <= 1140) {
                        return 79193373.913D;
                    }
                }
            }
        } else if (!field3.equals("255")) {
            if (field6 == null) {
                return 36513449.6964D;
            } else if (field6.equals("8103")) {
                if (field8 == null) {
                    return 2576000000D;
                } else if (field8 > 915) {
                    return 5328000000D;
                } else if (field8 <= 915) {
                    return 1200000000D;
                }
            } else if (!field6.equals("8103")) {
                if (field6.equals("6710")) {
                    if (field8 == null) {
                        return 399635505.623D;
                    } else if (field8 > 1110) {
                        if (field8 > 1260) {
                            return 6000000000D;
                        } else if (field8 <= 1260) {
                            return 2305200000D;
                        }
                    } else if (field8 <= 1110) {
                        if (field8 > 705) {
                            if (field7 == null) {
                                return 920309666.083D;
                            } else if (field7.equals("6714")) {
                                return 337288800D;
                            } else if (!field7.equals("6714")) {
                                if (field8 > 870) {
                                    if (field8 > 945) {
                                        if (field8 > 1020) {
                                            return 1091483748.25D;
                                        } else if (field8 <= 1020) {
                                            return 1491781200D;
                                        }
                                    } else if (field8 <= 945) {
                                        return 384000000D;
                                    }
                                } else if (field8 <= 870) {
                                    if (field8 > 810) {
                                        return 1248058800D;
                                    } else if (field8 <= 810) {
                                        return 1069242000D;
                                    }
                                }
                            }
                        } else if (field8 <= 705) {
                            if (field8 > 375) {
                                return 99261141.6923D;
                            } else if (field8 <= 375) {
                                return 20183250.8472D;
                            }
                        }
                    }
                } else if (!field6.equals("6710")) {
                    if (field4 == null) {
                        return 33988146.752D;
                    } else if (field4.equals("7936")) {
                        return 3854846400D;
                    } else if (!field4.equals("7936")) {
                        if (field3.equals("7432")) {
                            if (field8 == null) {
                                return 428317733.633D;
                            } else if (field8 > 2340) {
                                if (field4.equals("0")) {
                                    return 11520000000D;
                                } else if (!field4.equals("0")) {
                                    return 59120000D;
                                }
                            } else if (field8 <= 2340) {
                                if (field8 > 105) {
                                    return 29451496.2174D;
                                } else if (field8 <= 105) {
                                    return 125215021.333D;
                                }
                            }
                        } else if (!field3.equals("7432")) {
                            if (field8 == null) {
                                return 32718774.7965D;
                            } else if (field8 > 436) {
                                if (field1 == null) {
                                    return 56372399.7699D;
                                } else if (field1.equals("6346")) {
                                    if (field6.equals("6924")) {
                                        return 1982874019D;
                                    } else if (!field6.equals("6924")) {
                                        if (field7 == null) {
                                            return 160044190.419D;
                                        } else if (field7.equals("6603")) {
                                            return 1714273200D;
                                        } else if (!field7.equals("6603")) {
                                            if (field8 > 787) {
                                                if (field6.equals("6723")) {
                                                    if (field7.equals("0")) {
                                                        return 3004089600D;
                                                    } else if (!field7.equals("0")) {
                                                        if (field7.equals("6725")) {
                                                            if (field8 > 1095) {
                                                                return 916825923D;
                                                            } else if (field8 <= 1095) {
                                                                return 108000000D;
                                                            }
                                                        } else if (!field7.equals("6725")) {
                                                            return 28800000D;
                                                        }
                                                    }
                                                } else if (!field6.equals("6723")) {
                                                    if (field7.equals("6594")) {
                                                        return 3251212800D;
                                                    } else if (!field7.equals("6594")) {
                                                        if (field5 == null) {
                                                            return 174176205.014D;
                                                        } else if (field5.equals("6482")) {
                                                            return 1525200000D;
                                                        } else if (!field5.equals("6482")) {
                                                            if (field3.equals("7093")) {
                                                                return 1073650800D;
                                                            } else if (!field3.equals("7093")) {
                                                                if (field8 > 1665) {
                                                                    return 25055838.9655D;
                                                                } else if (field8 <= 1665) {
                                                                    if (field4.equals("7162")) {
                                                                        if (field8 > 1170) {
                                                                            if (field8 > 1350) {
                                                                                return 18000000D;
                                                                            } else if (field8 <= 1350) {
                                                                                return 2880000000D;
                                                                            }
                                                                        } else if (field8 <= 1170) {
                                                                            return 142134979.6D;
                                                                        }
                                                                    } else if (!field4.equals("7162")) {
                                                                        if (field8 > 865) {
                                                                            if (field3.equals("0")) {
                                                                                if (field8 > 1350) {
                                                                                    return 668189600D;
                                                                                } else if (field8 <= 1350) {
                                                                                    if (field2 == null) {
                                                                                        return 184258132.783D;
                                                                                    } else if (field2.equals("6347")) {
                                                                                        return 882000000D;
                                                                                    } else if (!field2.equals("6347")) {
                                                                                        if (field8 > 1005) {
                                                                                            return 214176617D;
                                                                                        } else if (field8 <= 1005) {
                                                                                            return 101180907D;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            } else if (!field3.equals("0")) {
                                                                                if (field4.equals("6404")) {
                                                                                    return 876000000D;
                                                                                } else if (!field4.equals("6404")) {
                                                                                    if (field3.equals("6647")) {
                                                                                        if (field7.equals("0")) {
                                                                                            if (field5.equals("6708")) {
                                                                                                if (field6.equals("0")) {
                                                                                                    return 2280000000D;
                                                                                                } else if (!field6.equals("0")) {
                                                                                                    return 17760000D;
                                                                                                }
                                                                                            } else if (!field5.equals("6708")) {
                                                                                                if (field8 > 1140) {
                                                                                                    return 60640000D;
                                                                                                } else if (field8 <= 1140) {
                                                                                                    if (field6.equals("6692")) {
                                                                                                        return 745200000D;
                                                                                                    } else if (!field6.equals("6692")) {
                                                                                                        if (field4.equals("6648")) {
                                                                                                            return 53947200D;
                                                                                                        } else if (!field4.equals("6648")) {
                                                                                                            if (field8 > 990) {
                                                                                                                return 393331800D;
                                                                                                            } else if (field8 <= 990) {
                                                                                                                return 246301412.444D;
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        } else if (!field7.equals("0")) {
                                                                                            return 39069528.3636D;
                                                                                        }
                                                                                    } else if (!field3.equals("6647")) {
                                                                                        if (field5.equals("6592")) {
                                                                                            return 394820880D;
                                                                                        } else if (!field5.equals("6592")) {
                                                                                            if (field6.equals("6888")) {
                                                                                                return 150000000D;
                                                                                            } else if (!field6.equals("6888")) {
                                                                                                return 41689102.4722D;
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        } else if (field8 <= 865) {
                                                                            if (field3.equals("6819")) {
                                                                                return 10622847.3333D;
                                                                            } else if (!field3.equals("6819")) {
                                                                                if (field3.equals("0")) {
                                                                                    return 181330000D;
                                                                                } else if (!field3.equals("0")) {
                                                                                    if (field4.equals("0")) {
                                                                                        return 1053375186D;
                                                                                    } else if (!field4.equals("0")) {
                                                                                        if (field3.equals("6647")) {
                                                                                            return 138931438.5D;
                                                                                        } else if (!field3.equals("6647")) {
                                                                                            return 484486610.5D;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (field8 <= 787) {
                                                if (field5 == null) {
                                                    return 127345910.827D;
                                                } else if (field5.equals("6528")) {
                                                    return 1200000000D;
                                                } else if (!field5.equals("6528")) {
                                                    if (field6.equals("6709")) {
                                                        return 1162404000D;
                                                    } else if (!field6.equals("6709")) {
                                                        if (field6.equals("6450")) {
                                                            return 1312819507D;
                                                        } else if (!field6.equals("6450")) {
                                                            if (field6.equals("6412")) {
                                                                if (field8 > 645) {
                                                                    return 94451529D;
                                                                } else if (field8 <= 645) {
                                                                    if (field8 > 502) {
                                                                        return 756000000D;
                                                                    } else if (field8 <= 502) {
                                                                        return 363301384D;
                                                                    }
                                                                }
                                                            } else if (!field6.equals("6412")) {
                                                                if (field8 > 558) {
                                                                    if (field6.equals("6535")) {
                                                                        if (field8 > 645) {
                                                                            return 300000000D;
                                                                        } else if (field8 <= 645) {
                                                                            return 1593600000D;
                                                                        }
                                                                    } else if (!field6.equals("6535")) {
                                                                        if (field5.equals("6543")) {
                                                                            return 661200000D;
                                                                        } else if (!field5.equals("6543")) {
                                                                            if (field5.equals("6541")) {
                                                                                return 630000000D;
                                                                            } else if (!field5.equals("6541")) {
                                                                                if (field6.equals("6893")) {
                                                                                    if (field8 > 615) {
                                                                                        return 60000000D;
                                                                                    } else if (field8 <= 615) {
                                                                                        return 588992400D;
                                                                                    }
                                                                                } else if (!field6.equals("6893")) {
                                                                                    if (field6.equals("6720")) {
                                                                                        if (field8 > 645) {
                                                                                            return 193200000D;
                                                                                        } else if (field8 <= 645) {
                                                                                            return 1103520000D;
                                                                                        }
                                                                                    } else if (!field6.equals("6720")) {
                                                                                        if (field4.equals("6665")) {
                                                                                            if (field8 > 672) {
                                                                                                if (field6.equals("6690")) {
                                                                                                    if (field8 > 725) {
                                                                                                        return 733170000D;
                                                                                                    } else if (field8 <= 725) {
                                                                                                        return 178650000D;
                                                                                                    }
                                                                                                } else if (!field6.equals("6690")) {
                                                                                                    return 101435048.025D;
                                                                                                }
                                                                                            } else if (field8 <= 672) {
                                                                                                if (field8 > 615) {
                                                                                                    if (field6.equals("0")) {
                                                                                                        return 609818666.667D;
                                                                                                    } else if (!field6.equals("0")) {
                                                                                                        return 131306400D;
                                                                                                    }
                                                                                                } else if (field8 <= 615) {
                                                                                                    if (field6.equals("6673")) {
                                                                                                        return 588992400D;
                                                                                                    } else if (!field6.equals("6673")) {
                                                                                                        return 90804271.6471D;
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        } else if (!field4.equals("6665")) {
                                                                                            if (field3.equals("0")) {
                                                                                                if (field2 == null) {
                                                                                                    return 178550144.542D;
                                                                                                } else if (field2.equals("6347")) {
                                                                                                    if (field8 > 630) {
                                                                                                        return 961200000D;
                                                                                                    } else if (field8 <= 630) {
                                                                                                        return 21788329D;
                                                                                                    }
                                                                                                } else if (!field2.equals("6347")) {
                                                                                                    if (field8 > 615) {
                                                                                                        if (field8 > 690) {
                                                                                                            if (field8 > 725) {
                                                                                                                if (field8 > 740) {
                                                                                                                    return 32280000D;
                                                                                                                } else if (field8 <= 740) {
                                                                                                                    return 614400000D;
                                                                                                                }
                                                                                                            } else if (field8 <= 725) {
                                                                                                                return 85614317.0526D;
                                                                                                            }
                                                                                                        } else if (field8 <= 690) {
                                                                                                            if (field8 > 645) {
                                                                                                                return 359760000D;
                                                                                                            } else if (field8 <= 645) {
                                                                                                                return 585600000D;
                                                                                                            }
                                                                                                        }
                                                                                                    } else if (field8 <= 615) {
                                                                                                        if (field8 > 588) {
                                                                                                            return 38857861.5D;
                                                                                                        } else if (field8 <= 588) {
                                                                                                            if (field8 > 573) {
                                                                                                                return 347495103D;
                                                                                                            } else if (field8 <= 573) {
                                                                                                                return 118565921.333D;
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            } else if (!field3.equals("0")) {
                                                                                                if (field4.equals("6622")) {
                                                                                                    return 551490000D;
                                                                                                } else if (!field4.equals("6622")) {
                                                                                                    if (field5.equals("6441")) {
                                                                                                        return 229744933.333D;
                                                                                                    } else if (!field5.equals("6441")) {
                                                                                                        if (field6.equals("6612")) {
                                                                                                            return 444000000D;
                                                                                                        } else if (!field6.equals("6612")) {
                                                                                                            if (field5.equals("6660")) {
                                                                                                                if (field8 > 645) {
                                                                                                                    if (field8 > 690) {
                                                                                                                        return 214721824.5D;
                                                                                                                    } else if (field8 <= 690) {
                                                                                                                        return 534600000D;
                                                                                                                    }
                                                                                                                } else if (field8 <= 645) {
                                                                                                                    return 43983840D;
                                                                                                                }
                                                                                                            } else if (!field5.equals("6660")) {
                                                                                                                if (field8 > 605) {
                                                                                                                    return 61572769.9417D;
                                                                                                                } else if (field8 <= 605) {
                                                                                                                    if (field4.equals("0")) {
                                                                                                                        return 304059540D;
                                                                                                                    } else if (!field4.equals("0")) {
                                                                                                                        return 106584935.054D;
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                } else if (field8 <= 558) {
                                                                    if (field7.equals("0")) {
                                                                        if (field6.equals("6886")) {
                                                                            return 450000000D;
                                                                        } else if (!field6.equals("6886")) {
                                                                            if (field8 > 465) {
                                                                                if (field4.equals("6627")) {
                                                                                    return 335247600D;
                                                                                } else if (!field4.equals("6627")) {
                                                                                    if (field6.equals("6410")) {
                                                                                        return 324000000D;
                                                                                    } else if (!field6.equals("6410")) {
                                                                                        if (field5.equals("6541")) {
                                                                                            return 273600000D;
                                                                                        } else if (!field5.equals("6541")) {
                                                                                            if (field3.equals("7153")) {
                                                                                                if (field8 > 525) {
                                                                                                    return 42049284.5D;
                                                                                                } else if (field8 <= 525) {
                                                                                                    return 430800000D;
                                                                                                }
                                                                                            } else if (!field3.equals("7153")) {
                                                                                                if (field5.equals("6689")) {
                                                                                                    if (field6.equals("6692")) {
                                                                                                        return 67185456.3D;
                                                                                                    } else if (!field6.equals("6692")) {
                                                                                                        return 145636595.667D;
                                                                                                    }
                                                                                                } else if (!field5.equals("6689")) {
                                                                                                    if (field6.equals("6535")) {
                                                                                                        if (field8 > 490) {
                                                                                                            return 85680000D;
                                                                                                        } else if (field8 <= 490) {
                                                                                                            return 444000000D;
                                                                                                        }
                                                                                                    } else if (!field6.equals("6535")) {
                                                                                                        if (field5.equals("6543")) {
                                                                                                            return 175196201.333D;
                                                                                                        } else if (!field5.equals("6543")) {
                                                                                                            return 92406998.72D;
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            } else if (field8 <= 465) {
                                                                                if (field2 == null) {
                                                                                    return 74200010.422D;
                                                                                } else if (field2.equals("6347")) {
                                                                                    return 432189945.5D;
                                                                                } else if (!field2.equals("6347")) {
                                                                                    if (field4.equals("6542")) {
                                                                                        return 390000000D;
                                                                                    } else if (!field4.equals("6542")) {
                                                                                        if (field5.equals("6532")) {
                                                                                            return 240000000D;
                                                                                        } else if (!field5.equals("6532")) {
                                                                                            return 61090588.8942D;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    } else if (!field7.equals("0")) {
                                                                        if (field7.equals("6445")) {
                                                                            return 276000000D;
                                                                        } else if (!field7.equals("6445")) {
                                                                            if (field4.equals("6568")) {
                                                                                return 159840000D;
                                                                            } else if (!field4.equals("6568")) {
                                                                                if (field7.equals("6437")) {
                                                                                    return 198000000D;
                                                                                } else if (!field7.equals("6437")) {
                                                                                    return 29221062.2167D;
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else if (!field1.equals("6346")) {
                                    if (field2 == null) {
                                        return 39302893.1929D;
                                    } else if (field2.equals("7892")) {
                                        if (field3.equals("0")) {
                                            if (field8 > 1425) {
                                                if (field8 > 1470) {
                                                    return 16290000D;
                                                } else if (field8 <= 1470) {
                                                    return 604871362.613D;
                                                }
                                            } else if (field8 <= 1425) {
                                                if (field8 > 690) {
                                                    return 36560482.7586D;
                                                } else if (field8 <= 690) {
                                                    if (field8 > 600) {
                                                        return 820920000D;
                                                    } else if (field8 <= 600) {
                                                        return 6300000D;
                                                    }
                                                }
                                            }
                                        } else if (!field3.equals("0")) {
                                            return 18802084.6604D;
                                        }
                                    } else if (!field2.equals("7892")) {
                                        if (field3.equals("7962")) {
                                            if (field8 > 3600) {
                                                if (field8 > 4860) {
                                                    return 79867920D;
                                                } else if (field8 <= 4860) {
                                                    return 2100000000D;
                                                }
                                            } else if (field8 <= 3600) {
                                                return 22200000D;
                                            }
                                        } else if (!field3.equals("7962")) {
                                            if (field4.equals("8450")) {
                                                if (field8 > 1110) {
                                                    if (field8 > 1620) {
                                                        if (field8 > 1890) {
                                                            return 11499999D;
                                                        } else if (field8 <= 1890) {
                                                            return 467466000D;
                                                        }
                                                    } else if (field8 <= 1620) {
                                                        return 22934640D;
                                                    }
                                                } else if (field8 <= 1110) {
                                                    if (field8 > 900) {
                                                        return 447360266.667D;
                                                    } else if (field8 <= 900) {
                                                        return 95880000D;
                                                    }
                                                }
                                            } else if (!field4.equals("8450")) {
                                                if (field4.equals("7378")) {
                                                    return 384000000D;
                                                } else if (!field4.equals("7378")) {
                                                    if (field4.equals("7488")) {
                                                        if (field8 > 1590) {
                                                            return 1320000000D;
                                                        } else if (field8 <= 1590) {
                                                            return 17040000D;
                                                        }
                                                    } else if (!field4.equals("7488")) {
                                                        if (field5 == null) {
                                                            return 31711723.6737D;
                                                        } else if (field5.equals("3333")) {
                                                            if (field8 > 1680) {
                                                                return 9072000D;
                                                            } else if (field8 <= 1680) {
                                                                if (field6.equals("0")) {
                                                                    return 1764000000D;
                                                                } else if (!field6.equals("0")) {
                                                                    return 37200000D;
                                                                }
                                                            }
                                                        } else if (!field5.equals("3333")) {
                                                            if (field3.equals("1588")) {
                                                                return 442243584D;
                                                            } else if (!field3.equals("1588")) {
                                                                if (field8 > 1716) {
                                                                    if (field2.equals("3431")) {
                                                                        return 1440000000D;
                                                                    } else if (!field2.equals("3431")) {
                                                                        if (field3.equals("3340")) {
                                                                            return 808800000D;
                                                                        } else if (!field3.equals("3340")) {
                                                                            if (field2.equals("8451")) {
                                                                                if (field8 > 1815) {
                                                                                    return 8508633.33333D;
                                                                                } else if (field8 <= 1815) {
                                                                                    if (field3.equals("0")) {
                                                                                        return 247529765.812D;
                                                                                    } else if (!field3.equals("0")) {
                                                                                        return 15960000D;
                                                                                    }
                                                                                }
                                                                            } else if (!field2.equals("8451")) {
                                                                                if (field3.equals("7729")) {
                                                                                    return 276000000D;
                                                                                } else if (!field3.equals("7729")) {
                                                                                    if (field1.equals("8039")) {
                                                                                        return 368400000D;
                                                                                    } else if (!field1.equals("8039")) {
                                                                                        if (field4.equals("5362")) {
                                                                                            return 420000000D;
                                                                                        } else if (!field4.equals("5362")) {
                                                                                            if (field5.equals("8012")) {
                                                                                                return 192000000D;
                                                                                            } else if (!field5.equals("8012")) {
                                                                                                if (field8 > 2130) {
                                                                                                    if (field3.equals("7315")) {
                                                                                                        if (field8 > 2265) {
                                                                                                            return 26676000D;
                                                                                                        } else if (field8 <= 2265) {
                                                                                                            return 468000000D;
                                                                                                        }
                                                                                                    } else if (!field3.equals("7315")) {
                                                                                                        if (field2.equals("0")) {
                                                                                                            if (field8 > 2565) {
                                                                                                                return 228100000D;
                                                                                                            } else if (field8 <= 2565) {
                                                                                                                return 52059259.2593D;
                                                                                                            }
                                                                                                        } else if (!field2.equals("0")) {
                                                                                                            if (field4.equals("8443")) {
                                                                                                                return 171665640D;
                                                                                                            } else if (!field4.equals("8443")) {
                                                                                                                if (field3.equals("7941")) {
                                                                                                                    return 237575467.5D;
                                                                                                                } else if (!field3.equals("7941")) {
                                                                                                                    if (field2.equals("9081")) {
                                                                                                                        if (field5.equals("0")) {
                                                                                                                            if (field8 > 2925) {
                                                                                                                                return 89049230.7692D;
                                                                                                                            } else if (field8 <= 2925) {
                                                                                                                                if (field3.equals("0")) {
                                                                                                                                    return 336000000D;
                                                                                                                                } else if (!field3.equals("0")) {
                                                                                                                                    return 30000000D;
                                                                                                                                }
                                                                                                                            }
                                                                                                                        } else if (!field5.equals("0")) {
                                                                                                                            return 17338066.6667D;
                                                                                                                        }
                                                                                                                    } else if (!field2.equals("9081")) {
                                                                                                                        return 39518355.9708D;
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                } else if (field8 <= 2130) {
                                                                                                    if (field3.equals("8743")) {
                                                                                                        return 230000000D;
                                                                                                    } else if (!field3.equals("8743")) {
                                                                                                        if (field2.equals("8499")) {
                                                                                                            return 300000000D;
                                                                                                        } else if (!field2.equals("8499")) {
                                                                                                            if (field3.equals("9241")) {
                                                                                                                return 119200000D;
                                                                                                            } else if (!field3.equals("9241")) {
                                                                                                                if (field1.equals("7699")) {
                                                                                                                    return 180000000D;
                                                                                                                } else if (!field1.equals("7699")) {
                                                                                                                    return 30455663.2941D;
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                } else if (field8 <= 1716) {
                                                                    if (field3.equals("7701")) {
                                                                        return 261000000D;
                                                                    } else if (!field3.equals("7701")) {
                                                                        if (field5.equals("1107")) {
                                                                            return 382193025D;
                                                                        } else if (!field5.equals("1107")) {
                                                                            if (field4.equals("9205")) {
                                                                                return 600000000D;
                                                                            } else if (!field4.equals("9205")) {
                                                                                if (field6.equals("8102")) {
                                                                                    if (field8 > 585) {
                                                                                        if (field8 > 685) {
                                                                                            return 293959083D;
                                                                                        } else if (field8 <= 685) {
                                                                                            return 30000000D;
                                                                                        }
                                                                                    } else if (field8 <= 585) {
                                                                                        return 574725105D;
                                                                                    }
                                                                                } else if (!field6.equals("8102")) {
                                                                                    if (field3.equals("7327")) {
                                                                                        if (field8 > 1260) {
                                                                                            if (field4.equals("0")) {
                                                                                                return 301223820D;
                                                                                            } else if (!field4.equals("0")) {
                                                                                                return 18000000D;
                                                                                            }
                                                                                        } else if (field8 <= 1260) {
                                                                                            return 12058680D;
                                                                                        }
                                                                                    } else if (!field3.equals("7327")) {
                                                                                        if (field5.equals("6028")) {
                                                                                            return 564000000D;
                                                                                        } else if (!field5.equals("6028")) {
                                                                                            if (field4.equals("3098")) {
                                                                                                if (field8 > 585) {
                                                                                                    return 110666328.688D;
                                                                                                } else if (field8 <= 585) {
                                                                                                    return 29793483.6667D;
                                                                                                }
                                                                                            } else if (!field4.equals("3098")) {
                                                                                                if (field3.equals("8440")) {
                                                                                                    if (field8 > 1350) {
                                                                                                        return 19753835.5741D;
                                                                                                    } else if (field8 <= 1350) {
                                                                                                        if (field8 > 930) {
                                                                                                            return 242071239.5D;
                                                                                                        } else if (field8 <= 930) {
                                                                                                            if (field4.equals("8443")) {
                                                                                                                return 239520000D;
                                                                                                            } else if (!field4.equals("8443")) {
                                                                                                                return 40982071.8571D;
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                } else if (!field3.equals("8440")) {
                                                                                                    if (field5.equals("2937")) {
                                                                                                        if (field6.equals("0")) {
                                                                                                            if (field8 > 1080) {
                                                                                                                return 1740000000D;
                                                                                                            } else if (field8 <= 1080) {
                                                                                                                return 213270000D;
                                                                                                            }
                                                                                                        } else if (!field6.equals("0")) {
                                                                                                            return 32717375.8043D;
                                                                                                        }
                                                                                                    } else if (!field5.equals("2937")) {
                                                                                                        if (field2.equals("3556")) {
                                                                                                            return 360000000D;
                                                                                                        } else if (!field2.equals("3556")) {
                                                                                                            if (field6.equals("7495")) {
                                                                                                                return 348000000D;
                                                                                                            } else if (!field6.equals("7495")) {
                                                                                                                if (field4.equals("8791")) {
                                                                                                                    return 480000000D;
                                                                                                                } else if (!field4.equals("8791")) {
                                                                                                                    if (field2.equals("1587")) {
                                                                                                                        if (field8 > 1260) {
                                                                                                                            return 143453928.682D;
                                                                                                                        } else if (field8 <= 1260) {
                                                                                                                            return 44289258.2143D;
                                                                                                                        }
                                                                                                                    } else if (!field2.equals("1587")) {
                                                                                                                        if (field2.equals("7590")) {
                                                                                                                            return 444000000D;
                                                                                                                        } else if (!field2.equals("7590")) {
                                                                                                                            if (field8 > 1245) {
                                                                                                                                if (field4.equals("8693")) {
                                                                                                                                    return 369166666D;
                                                                                                                                } else if (!field4.equals("8693")) {
                                                                                                                                    if (field6.equals("1729")) {
                                                                                                                                        return 336000000D;
                                                                                                                                    } else if (!field6.equals("1729")) {
                                                                                                                                        if (field4.equals("8086")) {
                                                                                                                                            return 258666666.667D;
                                                                                                                                        } else if (!field4.equals("8086")) {
                                                                                                                                            if (field4.equals("2570")) {
                                                                                                                                                return 147675000D;
                                                                                                                                            } else if (!field4.equals("2570")) {
                                                                                                                                                if (field2.equals("3027")) {
                                                                                                                                                    if (field3.equals("0")) {
                                                                                                                                                        return 227789360D;
                                                                                                                                                    } else if (!field3.equals("0")) {
                                                                                                                                                        return 19273183.0638D;
                                                                                                                                                    }
                                                                                                                                                } else if (!field2.equals("3027")) {
                                                                                                                                                    if (field1.equals("2676")) {
                                                                                                                                                        return 11697994.5457D;
                                                                                                                                                    } else if (!field1.equals("2676")) {
                                                                                                                                                        if (field3.equals("9179")) {
                                                                                                                                                            return 240000000D;
                                                                                                                                                        } else if (!field3.equals("9179")) {
                                                                                                                                                            if (field3.equals("7408")) {
                                                                                                                                                                return 147768595D;
                                                                                                                                                            } else if (!field3.equals("7408")) {
                                                                                                                                                                if (field2.equals("8667")) {
                                                                                                                                                                    return 81664965.5172D;
                                                                                                                                                                } else if (!field2.equals("8667")) {
                                                                                                                                                                    if (field2.equals("8254")) {
                                                                                                                                                                        return 288000000D;
                                                                                                                                                                    } else if (!field2.equals("8254")) {
                                                                                                                                                                        if (field2.equals("7725")) {
                                                                                                                                                                            if (field3.equals("0")) {
                                                                                                                                                                                return 208950000D;
                                                                                                                                                                            } else if (!field3.equals("0")) {
                                                                                                                                                                                return 34691399.1143D;
                                                                                                                                                                            }
                                                                                                                                                                        } else if (!field2.equals("7725")) {
                                                                                                                                                                            if (field2.equals("7947")) {
                                                                                                                                                                                return 189600000D;
                                                                                                                                                                            } else if (!field2.equals("7947")) {
                                                                                                                                                                                return 27176677.6601D;
                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            } else if (field8 <= 1245) {
                                                                                                                                if (field4.equals("8462")) {
                                                                                                                                    return 315600000D;
                                                                                                                                } else if (!field4.equals("8462")) {
                                                                                                                                    if (field5.equals("5604")) {
                                                                                                                                        return 288000000D;
                                                                                                                                    } else if (!field5.equals("5604")) {
                                                                                                                                        if (field3.equals("8724")) {
                                                                                                                                            return 264000000D;
                                                                                                                                        } else if (!field3.equals("8724")) {
                                                                                                                                            if (field5.equals("248")) {
                                                                                                                                                return 249300000D;
                                                                                                                                            } else if (!field5.equals("248")) {
                                                                                                                                                if (field4.equals("7409")) {
                                                                                                                                                    return 297500000D;
                                                                                                                                                } else if (!field4.equals("7409")) {
                                                                                                                                                    if (field2.equals("1953")) {
                                                                                                                                                        return 46937282.2877D;
                                                                                                                                                    } else if (!field2.equals("1953")) {
                                                                                                                                                        if (field1.equals("231")) {
                                                                                                                                                            if (field2.equals("0")) {
                                                                                                                                                                if (field8 > 795) {
                                                                                                                                                                    return 756000000D;
                                                                                                                                                                } else if (field8 <= 795) {
                                                                                                                                                                    return 1740D;
                                                                                                                                                                }
                                                                                                                                                            } else if (!field2.equals("0")) {
                                                                                                                                                                return 27801469.9533D;
                                                                                                                                                            }
                                                                                                                                                        } else if (!field1.equals("231")) {
                                                                                                                                                            return 16715972.8258D;
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (field8 <= 436) {
                                if (field1 == null) {
                                    return 19078879.4837D;
                                } else if (field1.equals("6346")) {
                                    if (field8 > 215) {
                                        if (field6.equals("6497")) {
                                            if (field8 > 425) {
                                                return 620142955D;
                                            } else if (field8 <= 425) {
                                                if (field8 > 247) {
                                                    return 47843190.3333D;
                                                } else if (field8 <= 247) {
                                                    if (field8 > 242) {
                                                        return 165956684.2D;
                                                    } else if (field8 <= 242) {
                                                        return 278517759.385D;
                                                    }
                                                }
                                            }
                                        } else if (!field6.equals("6497")) {
                                            if (field7 == null) {
                                                return 46839142.9D;
                                            } else if (field7.equals("6776")) {
                                                return 732000000D;
                                            } else if (!field7.equals("6776")) {
                                                if (field3.equals("6819")) {
                                                    return 13240920.2407D;
                                                } else if (!field3.equals("6819")) {
                                                    if (field7.equals("6431")) {
                                                        if (field8 > 423) {
                                                            return 450000000D;
                                                        } else if (field8 <= 423) {
                                                            return 133200000D;
                                                        }
                                                    } else if (!field7.equals("6431")) {
                                                        if (field7.equals("6576")) {
                                                            return 255505131D;
                                                        } else if (!field7.equals("6576")) {
                                                            if (field6.equals("6490")) {
                                                                if (field8 > 285) {
                                                                    return 242624100D;
                                                                } else if (field8 <= 285) {
                                                                    return 23907311D;
                                                                }
                                                            } else if (!field6.equals("6490")) {
                                                                if (field8 > 386) {
                                                                    return 62284561.5643D;
                                                                } else if (field8 <= 386) {
                                                                    if (field7.equals("6462")) {
                                                                        return 230400000D;
                                                                    } else if (!field7.equals("6462")) {
                                                                        return 44477745.1815D;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else if (field8 <= 215) {
                                        if (field6.equals("6720")) {
                                            if (field8 > 45) {
                                                return 111982676.923D;
                                            } else if (field8 <= 45) {
                                                return 27967107.75D;
                                            }
                                        } else if (!field6.equals("6720")) {
                                            if (field8 > 155) {
                                                if (field6.equals("6709")) {
                                                    return 540000000D;
                                                } else if (!field6.equals("6709")) {
                                                    if (field6.equals("6723")) {
                                                        return 82896941.1304D;
                                                    } else if (!field6.equals("6723")) {
                                                        return 29825788.0408D;
                                                    }
                                                }
                                            } else if (field8 <= 155) {
                                                return 19045986.0026D;
                                            }
                                        }
                                    }
                                } else if (!field1.equals("6346")) {
                                    if (field6.equals("8102")) {
                                        if (field8 > 408) {
                                            if (field8 > 428) {
                                                return 125468612D;
                                            } else if (field8 <= 428) {
                                                return 871459317D;
                                            }
                                        } else if (field8 <= 408) {
                                            if (field8 > 152) {
                                                if (field8 > 355) {
                                                    return 60385617.5D;
                                                } else if (field8 <= 355) {
                                                    return 238489297D;
                                                }
                                            } else if (field8 <= 152) {
                                                return 15085321.3333D;
                                            }
                                        }
                                    } else if (!field6.equals("8102")) {
                                        if (field3.equals("5317")) {
                                            return 600000000D;
                                        } else if (!field3.equals("5317")) {
                                            if (field2 == null) {
                                                return 11638454.4086D;
                                            } else if (field2.equals("9081")) {
                                                if (field3.equals("0")) {
                                                    if (field8 > 375) {
                                                        return 2880000D;
                                                    } else if (field8 <= 375) {
                                                        if (field8 > 315) {
                                                            return 201731723.429D;
                                                        } else if (field8 <= 315) {
                                                            return 50000000D;
                                                        }
                                                    }
                                                } else if (!field3.equals("0")) {
                                                    return 16595107.1539D;
                                                }
                                            } else if (!field2.equals("9081")) {
                                                if (field2.equals("3027")) {
                                                    if (field3.equals("0")) {
                                                        return 128228716.13D;
                                                    } else if (!field3.equals("0")) {
                                                        if (field4.equals("3098")) {
                                                            return 51744585.4225D;
                                                        } else if (!field4.equals("3098")) {
                                                            return 7416666.13333D;
                                                        }
                                                    }
                                                } else if (!field2.equals("3027")) {
                                                    if (field3.equals("9133")) {
                                                        return 237792000D;
                                                    } else if (!field3.equals("9133")) {
                                                        if (field4.equals("2105")) {
                                                            return 300000000D;
                                                        } else if (!field4.equals("2105")) {
                                                            if (field8 > 142) {
                                                                if (field2.equals("405")) {
                                                                    return 78720328.7083D;
                                                                } else if (!field2.equals("405")) {
                                                                    if (field4.equals("8434")) {
                                                                        if (field8 > 330) {
                                                                            return 283685160D;
                                                                        } else if (field8 <= 330) {
                                                                            return 2499999D;
                                                                        }
                                                                    } else if (!field4.equals("8434")) {
                                                                        if (field7 == null) {
                                                                            return 14125351.2173D;
                                                                        } else if (field7.equals("3375")) {
                                                                            return 101388888.667D;
                                                                        } else if (!field7.equals("3375")) {
                                                                            if (field3.equals("7962")) {
                                                                                return 128306867D;
                                                                            } else if (!field3.equals("7962")) {
                                                                                if (field5 == null) {
                                                                                    return 13810816.4237D;
                                                                                } else if (field5.equals("9186")) {
                                                                                    return 99428571.4286D;
                                                                                } else if (!field5.equals("9186")) {
                                                                                    return 13658780.8364D;
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } else if (field8 <= 142) {
                                                                return 6525972.42874D;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
