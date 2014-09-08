package cz.opendata.tenderstats.dm;

public class Model1 implements Model {

    @Override
    public String predictField9(String field1, String field2, String field3, String field4, String field5, String field6, String field7, Double field8) {
        if (field1 == null) {
            return "12500000";
        } else if (field1.equals("6346")) {
            if (field8 == null) {
                return "25000000";
            } else if (field8 > 384) {
                if (field7 == null) {
                    return "250000000";
                } else if (field7.equals("6724")) {
                    return "1000000";
                } else if (!field7.equals("6724")) {
                    if (field8 > 563) {
                        if (field3 == null) {
                            return "0";
                        } else if (field3.equals("6647")) {
                            return "0";
                        } else if (!field3.equals("6647")) {
                            if (field8 > 1415) {
                                return "75000000";
                            } else if (field8 <= 1415) {
                                if (field8 > 725) {
                                    return "0";
                                } else if (field8 <= 725) {
                                    return "250000000";
                                }
                            }
                        }
                    } else if (field8 <= 563) {
                        if (field3 == null) {
                            return "250000000";
                        } else if (field3.equals("6819")) {
                            return "12500000";
                        } else if (!field3.equals("6819")) {
                            if (field7.equals("6559")) {
                                return "17500000";
                            } else if (!field7.equals("6559")) {
                                if (field8 > 426) {
                                    if (field3.equals("6943")) {
                                        return "25000000";
                                    } else if (!field3.equals("6943")) {
                                        if (field3.equals("6647")) {
                                            return "250000000";
                                        } else if (!field3.equals("6647")) {
                                            return "250000000";
                                        }
                                    }
                                } else if (field8 <= 426) {
                                    return "75000000";
                                }
                            }
                        }
                    }
                }
            } else if (field8 <= 384) {
                if (field8 > 155) {
                    if (field3 == null) {
                        return "25000000";
                    } else if (field3.equals("6819")) {
                        return "12500000";
                    } else if (!field3.equals("6819")) {
                        if (field6 == null) {
                            return "25000000";
                        } else if (field6.equals("0")) {
                            if (field8 > 211) {
                                if (field3.equals("0")) {
                                    if (field8 > 355) {
                                        return "25000000";
                                    } else if (field8 <= 355) {
                                        return "75000000";
                                    }
                                } else if (!field3.equals("0")) {
                                    if (field5 == null) {
                                        return "40000000";
                                    } else if (field5.equals("6708")) {
                                        return "50000000";
                                    } else if (!field5.equals("6708")) {
                                        if (field3.equals("6647")) {
                                            return "25000000";
                                        } else if (!field3.equals("6647")) {
                                            if (field8 > 265) {
                                                if (field2 == null) {
                                                    return "40000000";
                                                } else if (field2.equals("7015")) {
                                                    return "2000000";
                                                } else if (!field2.equals("7015")) {
                                                    if (field5.equals("6592")) {
                                                        return "35000000";
                                                    } else if (!field5.equals("6592")) {
                                                        if (field5.equals("6496")) {
                                                            return "12500000";
                                                        } else if (!field5.equals("6496")) {
                                                            return "40000000";
                                                        }
                                                    }
                                                }
                                            } else if (field8 <= 265) {
                                                return "20000000";
                                            }
                                        }
                                    }
                                }
                            } else if (field8 <= 211) {
                                if (field4 == null) {
                                    return "25000000";
                                } else if (field4.equals("6415")) {
                                    return "12500000";
                                } else if (!field4.equals("6415")) {
                                    if (field4.equals("7162")) {
                                        return "15000000";
                                    } else if (!field4.equals("7162")) {
                                        if (field3.equals("6647")) {
                                            return "75000000";
                                        } else if (!field3.equals("6647")) {
                                            if (field8 > 181) {
                                                return "20000000";
                                            } else if (field8 <= 181) {
                                                return "25000000";
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (!field6.equals("0")) {
                            if (field6.equals("6497")) {
                                return "75000000";
                            } else if (!field6.equals("6497")) {
                                if (field7 == null) {
                                    return "75000000";
                                } else if (field7.equals("6724")) {
                                    return "1500000";
                                } else if (!field7.equals("6724")) {
                                    if (field6.equals("6536")) {
                                        return "3000000";
                                    } else if (!field6.equals("6536")) {
                                        if (field8 > 181) {
                                            if (field7.equals("6713")) {
                                                return "9000000";
                                            } else if (!field7.equals("6713")) {
                                                if (field6.equals("6417")) {
                                                    return "100000000";
                                                } else if (!field6.equals("6417")) {
                                                    if (field3.equals("6403")) {
                                                        return "75000000";
                                                    } else if (!field3.equals("6403")) {
                                                        if (field3.equals("6943")) {
                                                            return "35000000";
                                                        } else if (!field3.equals("6943")) {
                                                            if (field6.equals("6692")) {
                                                                return "20000000";
                                                            } else if (!field6.equals("6692")) {
                                                                return "250000000";
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (field8 <= 181) {
                                            return "17500000";
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (field8 <= 155) {
                    if (field6 == null) {
                        return "12500000";
                    } else if (field6.equals("6720")) {
                        return "250000000";
                    } else if (!field6.equals("6720")) {
                        if (field8 > 68) {
                            if (field4 == null) {
                                return "12500000";
                            } else if (field4.equals("6568")) {
                                return "12500000";
                            } else if (!field4.equals("6568")) {
                                if (field3 == null) {
                                    return "12500000";
                                } else if (field3.equals("6819")) {
                                    return "7000000";
                                } else if (!field3.equals("6819")) {
                                    if (field8 > 120) {
                                        if (field4.equals("6415")) {
                                            return "7000000";
                                        } else if (!field4.equals("6415")) {
                                            if (field3.equals("6403")) {
                                                return "30000000";
                                            } else if (!field3.equals("6403")) {
                                                if (field5 == null) {
                                                    return "12500000";
                                                } else if (field5.equals("6708")) {
                                                    return "25000000";
                                                } else if (!field5.equals("6708")) {
                                                    return "12500000";
                                                }
                                            }
                                        }
                                    } else if (field8 <= 120) {
                                        if (field6.equals("6733")) {
                                            return "17500000";
                                        } else if (!field6.equals("6733")) {
                                            if (field3.equals("7062")) {
                                                return "50000000";
                                            } else if (!field3.equals("7062")) {
                                                if (field8 > 79) {
                                                    if (field8 > 93) {
                                                        if (field2 == null) {
                                                            return "12500000";
                                                        } else if (field2.equals("6402")) {
                                                            if (field4.equals("6415")) {
                                                                return "15000000";
                                                            } else if (!field4.equals("6415")) {
                                                                return "12500000";
                                                            }
                                                        } else if (!field2.equals("6402")) {
                                                            return "40000000";
                                                        }
                                                    } else if (field8 <= 93) {
                                                        if (field6.equals("6723")) {
                                                            return "25000000";
                                                        } else if (!field6.equals("6723")) {
                                                            if (field2 == null) {
                                                                return "12500000";
                                                            } else if (field2.equals("0")) {
                                                                return "25000000";
                                                            } else if (!field2.equals("0")) {
                                                                if (field3.equals("6647")) {
                                                                    return "12500000";
                                                                } else if (!field3.equals("6647")) {
                                                                    return "12500000";
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else if (field8 <= 79) {
                                                    return "25000000";
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (field8 <= 68) {
                            if (field4 == null) {
                                return "12500000";
                            } else if (field4.equals("6707")) {
                                if (field7 == null) {
                                    return "12500000";
                                } else if (field7.equals("6736")) {
                                    return "17500000";
                                } else if (!field7.equals("6736")) {
                                    if (field8 > 32) {
                                        return "12500000";
                                    } else if (field8 <= 32) {
                                        return "12500000";
                                    }
                                }
                            } else if (!field4.equals("6707")) {
                                if (field8 > 43) {
                                    return "12500000";
                                } else if (field8 <= 43) {
                                    return "250000";
                                }
                            }
                        }
                    }
                }
            }
        } else if (!field1.equals("6346")) {
            if (field8 == null) {
                return "1000000";
            } else if (field8 > 127) {
                if (field8 > 769) {
                    if (field2 == null) {
                        return "12500000";
                    } else if (field2.equals("8926")) {
                        return "1000000";
                    } else if (!field2.equals("8926")) {
                        if (field2.equals("2677")) {
                            if (field3 == null) {
                                return "1000000";
                            } else if (field3.equals("2783")) {
                                return "250000";
                            } else if (!field3.equals("2783")) {
                                return "1000000";
                            }
                        } else if (!field2.equals("2677")) {
                            if (field3 == null) {
                                return "12500000";
                            } else if (field3.equals("8094")) {
                                return "6000000";
                            } else if (!field3.equals("8094")) {
                                if (field8 > 1523) {
                                    if (field8 > 1836) {
                                        if (field2.equals("0")) {
                                            return "250000000";
                                        } else if (!field2.equals("0")) {
                                            if (field3.equals("7952")) {
                                                return "10000000";
                                            } else if (!field3.equals("7952")) {
                                                return "0";
                                            }
                                        }
                                    } else if (field8 <= 1836) {
                                        if (field5 == null) {
                                            return "250000000";
                                        } else if (field5.equals("0")) {
                                            if (field1.equals("8511")) {
                                                return "10000000";
                                            } else if (!field1.equals("8511")) {
                                                if (field1.equals("8259")) {
                                                    return "15000000";
                                                } else if (!field1.equals("8259")) {
                                                    return "250000000";
                                                }
                                            }
                                        } else if (!field5.equals("0")) {
                                            return "17500000";
                                        }
                                    }
                                } else if (field8 <= 1523) {
                                    if (field2.equals("8667")) {
                                        return "100000000";
                                    } else if (!field2.equals("8667")) {
                                        if (field8 > 1425) {
                                            if (field1.equals("1285")) {
                                                return "17500000";
                                            } else if (!field1.equals("1285")) {
                                                if (field2.equals("3318")) {
                                                    return "25000000";
                                                } else if (!field2.equals("3318")) {
                                                    if (field1.equals("4656")) {
                                                        return "100000000";
                                                    } else if (!field1.equals("4656")) {
                                                        if (field2.equals("1953")) {
                                                            return "250000000";
                                                        } else if (!field2.equals("1953")) {
                                                            if (field3.equals("327")) {
                                                                return "4000000";
                                                            } else if (!field3.equals("327")) {
                                                                if (field2.equals("8716")) {
                                                                    return "12500000";
                                                                } else if (!field2.equals("8716")) {
                                                                    if (field2.equals("9081")) {
                                                                        return "75000000";
                                                                    } else if (!field2.equals("9081")) {
                                                                        if (field1.equals("231")) {
                                                                            return "40000000";
                                                                        } else if (!field1.equals("231")) {
                                                                            if (field2.equals("1395")) {
                                                                                return "6000000";
                                                                            } else if (!field2.equals("1395")) {
                                                                                if (field1.equals("7365")) {
                                                                                    return "8000000";
                                                                                } else if (!field1.equals("7365")) {
                                                                                    if (field2.equals("8688")) {
                                                                                        return "12500000";
                                                                                    } else if (!field2.equals("8688")) {
                                                                                        if (field2.equals("8831")) {
                                                                                            return "12500000";
                                                                                        } else if (!field2.equals("8831")) {
                                                                                            if (field1.equals("8715")) {
                                                                                                return "4000000";
                                                                                            } else if (!field1.equals("8715")) {
                                                                                                if (field4 == null) {
                                                                                                    return "12500000";
                                                                                                } else if (field4.equals("0")) {
                                                                                                    if (field2.equals("7892")) {
                                                                                                        return "0";
                                                                                                    } else if (!field2.equals("7892")) {
                                                                                                        if (field1.equals("9067")) {
                                                                                                            return "75000000";
                                                                                                        } else if (!field1.equals("9067")) {
                                                                                                            if (field3.equals("0")) {
                                                                                                                return "250000000";
                                                                                                            } else if (!field3.equals("0")) {
                                                                                                                if (field1.equals("7167")) {
                                                                                                                    return "0";
                                                                                                                } else if (!field1.equals("7167")) {
                                                                                                                    if (field3.equals("8066")) {
                                                                                                                        return "12500000";
                                                                                                                    } else if (!field3.equals("8066")) {
                                                                                                                        return "12500000";
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                } else if (!field4.equals("0")) {
                                                                                                    if (field2.equals("8266")) {
                                                                                                        return "25000000";
                                                                                                    } else if (!field2.equals("8266")) {
                                                                                                        if (field1.equals("7878")) {
                                                                                                            return "7000000";
                                                                                                        } else if (!field1.equals("7878")) {
                                                                                                            if (field2.equals("9384")) {
                                                                                                                return "250000000";
                                                                                                            } else if (!field2.equals("9384")) {
                                                                                                                if (field1.equals("8259")) {
                                                                                                                    return "50000000";
                                                                                                                } else if (!field1.equals("8259")) {
                                                                                                                    if (field4.equals("8207")) {
                                                                                                                        return "10000000";
                                                                                                                    } else if (!field4.equals("8207")) {
                                                                                                                        if (field6 == null) {
                                                                                                                            return "25000000";
                                                                                                                        } else if (field6.equals("1947")) {
                                                                                                                            return "20000000";
                                                                                                                        } else if (!field6.equals("1947")) {
                                                                                                                            if (field4.equals("3143")) {
                                                                                                                                return "250000000";
                                                                                                                            } else if (!field4.equals("3143")) {
                                                                                                                                if (field1.equals("7167")) {
                                                                                                                                    return "25000000";
                                                                                                                                } else if (!field1.equals("7167")) {
                                                                                                                                    if (field2.equals("9240")) {
                                                                                                                                        return "7000000";
                                                                                                                                    } else if (!field2.equals("9240")) {
                                                                                                                                        if (field3.equals("7982")) {
                                                                                                                                            return "5000000";
                                                                                                                                        } else if (!field3.equals("7982")) {
                                                                                                                                            if (field5 == null) {
                                                                                                                                                return "25000000";
                                                                                                                                            } else if (field5.equals("0")) {
                                                                                                                                                return "15000000";
                                                                                                                                            } else if (!field5.equals("0")) {
                                                                                                                                                return "25000000";
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
                                        } else if (field8 <= 1425) {
                                            if (field1.equals("8969")) {
                                                if (field8 > 1065) {
                                                    return "12500000";
                                                } else if (field8 <= 1065) {
                                                    return "6000000";
                                                }
                                            } else if (!field1.equals("8969")) {
                                                if (field3.equals("8689")) {
                                                    return "2500000";
                                                } else if (!field3.equals("8689")) {
                                                    if (field1.equals("8259")) {
                                                        return "25000000";
                                                    } else if (!field1.equals("8259")) {
                                                        if (field3.equals("0")) {
                                                            if (field2.equals("7892")) {
                                                                return "250000000";
                                                            } else if (!field2.equals("7892")) {
                                                                return "7000000";
                                                            }
                                                        } else if (!field3.equals("0")) {
                                                            if (field1.equals("231")) {
                                                                return "100000000";
                                                            } else if (!field1.equals("231")) {
                                                                if (field1.equals("8715")) {
                                                                    return "12500000";
                                                                } else if (!field1.equals("8715")) {
                                                                    if (field4 == null) {
                                                                        return "25000000";
                                                                    } else if (field4.equals("0")) {
                                                                        return "5000000";
                                                                    } else if (!field4.equals("0")) {
                                                                        if (field1.equals("9067")) {
                                                                            return "25000000";
                                                                        } else if (!field1.equals("9067")) {
                                                                            if (field7 == null) {
                                                                                return "50000000";
                                                                            } else if (field7.equals("0")) {
                                                                                if (field3.equals("7893")) {
                                                                                    return "50000000";
                                                                                } else if (!field3.equals("7893")) {
                                                                                    if (field1.equals("2676")) {
                                                                                        return "7000000";
                                                                                    } else if (!field1.equals("2676")) {
                                                                                        if (field2.equals("1953")) {
                                                                                            return "50000000";
                                                                                        } else if (!field2.equals("1953")) {
                                                                                            return "6000000";
                                                                                        }
                                                                                    }
                                                                                }
                                                                            } else if (!field7.equals("0")) {
                                                                                return "100000000";
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
                } else if (field8 <= 769) {
                    if (field3 == null) {
                        return "1000000";
                    } else if (field3.equals("8094")) {
                        return "500000";
                    } else if (!field3.equals("8094")) {
                        if (field4 == null) {
                            return "1000000";
                        } else if (field4.equals("8415")) {
                            return "1000000";
                        } else if (!field4.equals("8415")) {
                            if (field1.equals("9369")) {
                                return "1000000";
                            } else if (!field1.equals("9369")) {
                                if (field3.equals("8668")) {
                                    return "1000000";
                                } else if (!field3.equals("8668")) {
                                    if (field3.equals("327")) {
                                        return "250000";
                                    } else if (!field3.equals("327")) {
                                        if (field1.equals("8914")) {
                                            if (field8 > 405) {
                                                return "1500000";
                                            } else if (field8 <= 405) {
                                                return "250000";
                                            }
                                        } else if (!field1.equals("8914")) {
                                            if (field4.equals("3098")) {
                                                return "250000000";
                                            } else if (!field4.equals("3098")) {
                                                if (field5 == null) {
                                                    return "6000000";
                                                } else if (field5.equals("8100")) {
                                                    return "0";
                                                } else if (!field5.equals("8100")) {
                                                    if (field8 > 525) {
                                                        if (field8 > 585) {
                                                            if (field1.equals("473")) {
                                                                return "1000000";
                                                            } else if (!field1.equals("473")) {
                                                                if (field4.equals("0")) {
                                                                    if (field1.equals("8511")) {
                                                                        return "7000000";
                                                                    } else if (!field1.equals("8511")) {
                                                                        if (field2 == null) {
                                                                            return "6000000";
                                                                        } else if (field2.equals("3027")) {
                                                                            return "50000000";
                                                                        } else if (!field2.equals("3027")) {
                                                                            if (field2.equals("8667")) {
                                                                                return "7000000";
                                                                            } else if (!field2.equals("8667")) {
                                                                                if (field1.equals("2676")) {
                                                                                    return "1500000";
                                                                                } else if (!field1.equals("2676")) {
                                                                                    if (field1.equals("1686")) {
                                                                                        return "3500000";
                                                                                    } else if (!field1.equals("1686")) {
                                                                                        if (field2.equals("7892")) {
                                                                                            return "4000000";
                                                                                        } else if (!field2.equals("7892")) {
                                                                                            if (field1.equals("8715")) {
                                                                                                return "6000000";
                                                                                            } else if (!field1.equals("8715")) {
                                                                                                if (field2.equals("9045")) {
                                                                                                    return "2000000";
                                                                                                } else if (!field2.equals("9045")) {
                                                                                                    if (field1.equals("8259")) {
                                                                                                        return "4000000";
                                                                                                    } else if (!field1.equals("8259")) {
                                                                                                        if (field1.equals("8075")) {
                                                                                                            return "35000000";
                                                                                                        } else if (!field1.equals("8075")) {
                                                                                                            if (field2.equals("4657")) {
                                                                                                                return "100000000";
                                                                                                            } else if (!field2.equals("4657")) {
                                                                                                                if (field1.equals("3317")) {
                                                                                                                    return "6000000";
                                                                                                                } else if (!field1.equals("3317")) {
                                                                                                                    if (field1.equals("231")) {
                                                                                                                        return "15000000";
                                                                                                                    } else if (!field1.equals("231")) {
                                                                                                                        return "2500000";
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
                                                                } else if (!field4.equals("0")) {
                                                                    if (field2 == null) {
                                                                        return "12500000";
                                                                    } else if (field2.equals("3027")) {
                                                                        return "1500000";
                                                                    } else if (!field2.equals("3027")) {
                                                                        if (field1.equals("8259")) {
                                                                            return "20000000";
                                                                        } else if (!field1.equals("8259")) {
                                                                            if (field3.equals("2926")) {
                                                                                return "12500000";
                                                                            } else if (!field3.equals("2926")) {
                                                                                if (field2.equals("1953")) {
                                                                                    return "12500000";
                                                                                } else if (!field2.equals("1953")) {
                                                                                    if (field2.equals("1587")) {
                                                                                        return "40000000";
                                                                                    } else if (!field2.equals("1587")) {
                                                                                        if (field1.equals("2676")) {
                                                                                            return "25000000";
                                                                                        } else if (!field1.equals("2676")) {
                                                                                            if (field3.equals("7893")) {
                                                                                                return "2500000";
                                                                                            } else if (!field3.equals("7893")) {
                                                                                                if (field1.equals("231")) {
                                                                                                    return "6000000";
                                                                                                } else if (!field1.equals("231")) {
                                                                                                    if (field2.equals("1687")) {
                                                                                                        return "12500000";
                                                                                                    } else if (!field2.equals("1687")) {
                                                                                                        if (field5.equals("0")) {
                                                                                                            return "7000000";
                                                                                                        } else if (!field5.equals("0")) {
                                                                                                            return "8000000";
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
                                                        } else if (field8 <= 585) {
                                                            return "75000000";
                                                        }
                                                    } else if (field8 <= 525) {
                                                        if (field8 > 261) {
                                                            if (field8 > 307) {
                                                                if (field4.equals("2784")) {
                                                                    return "2500000";
                                                                } else if (!field4.equals("2784")) {
                                                                    if (field3.equals("406")) {
                                                                        return "250000000";
                                                                    } else if (!field3.equals("406")) {
                                                                        if (field1.equals("231")) {
                                                                            if (field3.equals("255")) {
                                                                                return "0";
                                                                            } else if (!field3.equals("255")) {
                                                                                if (field2 == null) {
                                                                                    return "3000000";
                                                                                } else if (field2.equals("308")) {
                                                                                    return "4000000";
                                                                                } else if (!field2.equals("308")) {
                                                                                    return "3000000";
                                                                                }
                                                                            }
                                                                        } else if (!field1.equals("231")) {
                                                                            if (field1.equals("473")) {
                                                                                return "7000000";
                                                                            } else if (!field1.equals("473")) {
                                                                                if (field1.equals("8969")) {
                                                                                    return "2500000";
                                                                                } else if (!field1.equals("8969")) {
                                                                                    if (field8 > 362) {
                                                                                        if (field1.equals("3785")) {
                                                                                            return "250000";
                                                                                        } else if (!field1.equals("3785")) {
                                                                                            if (field8 > 390) {
                                                                                                return "6000000";
                                                                                            } else if (field8 <= 390) {
                                                                                                return "50000000";
                                                                                            }
                                                                                        }
                                                                                    } else if (field8 <= 362) {
                                                                                        if (field1.equals("3317")) {
                                                                                            return "2500000";
                                                                                        } else if (!field1.equals("3317")) {
                                                                                            if (field2 == null) {
                                                                                                return "6000000";
                                                                                            } else if (field2.equals("3027")) {
                                                                                                return "1500000";
                                                                                            } else if (!field2.equals("3027")) {
                                                                                                if (field2.equals("1953")) {
                                                                                                    return "3500000";
                                                                                                } else if (!field2.equals("1953")) {
                                                                                                    if (field2.equals("8667")) {
                                                                                                        return "1000000";
                                                                                                    } else if (!field2.equals("8667")) {
                                                                                                        if (field1.equals("8075")) {
                                                                                                            return "2500000";
                                                                                                        } else if (!field1.equals("8075")) {
                                                                                                            if (field2.equals("5808")) {
                                                                                                                return "2000000";
                                                                                                            } else if (!field2.equals("5808")) {
                                                                                                                if (field1.equals("325")) {
                                                                                                                    return "12500000";
                                                                                                                } else if (!field1.equals("325")) {
                                                                                                                    if (field3.equals("8432")) {
                                                                                                                        return "1500000";
                                                                                                                    } else if (!field3.equals("8432")) {
                                                                                                                        if (field1.equals("8715")) {
                                                                                                                            return "20000000";
                                                                                                                        } else if (!field1.equals("8715")) {
                                                                                                                            if (field8 > 347) {
                                                                                                                                if (field3.equals("199")) {
                                                                                                                                    return "15000000";
                                                                                                                                } else if (!field3.equals("199")) {
                                                                                                                                    if (field1.equals("1285")) {
                                                                                                                                        return "250000";
                                                                                                                                    } else if (!field1.equals("1285")) {
                                                                                                                                        if (field3.equals("2783")) {
                                                                                                                                            return "2000000";
                                                                                                                                        } else if (!field3.equals("2783")) {
                                                                                                                                            if (field1.equals("7950")) {
                                                                                                                                                return "250000";
                                                                                                                                            } else if (!field1.equals("7950")) {
                                                                                                                                                if (field3.equals("1601")) {
                                                                                                                                                    return "12500000";
                                                                                                                                                } else if (!field3.equals("1601")) {
                                                                                                                                                    if (field2.equals("1587")) {
                                                                                                                                                        return "7000000";
                                                                                                                                                    } else if (!field2.equals("1587")) {
                                                                                                                                                        if (field1.equals("1394")) {
                                                                                                                                                            return "6000000";
                                                                                                                                                        } else if (!field1.equals("1394")) {
                                                                                                                                                            if (field1.equals("5128")) {
                                                                                                                                                                return "10000000";
                                                                                                                                                            } else if (!field1.equals("5128")) {
                                                                                                                                                                if (field4.equals("2935")) {
                                                                                                                                                                    return "75000000";
                                                                                                                                                                } else if (!field4.equals("2935")) {
                                                                                                                                                                    if (field1.equals("5807")) {
                                                                                                                                                                        return "5000000";
                                                                                                                                                                    } else if (!field1.equals("5807")) {
                                                                                                                                                                        if (field5.equals("0")) {
                                                                                                                                                                            if (field3.equals("0")) {
                                                                                                                                                                                return "1000000";
                                                                                                                                                                            } else if (!field3.equals("0")) {
                                                                                                                                                                                if (field2.equals("1687")) {
                                                                                                                                                                                    return "5000000";
                                                                                                                                                                                } else if (!field2.equals("1687")) {
                                                                                                                                                                                    if (field1.equals("2676")) {
                                                                                                                                                                                        return "6000000";
                                                                                                                                                                                    } else if (!field1.equals("2676")) {
                                                                                                                                                                                        if (field1.equals("4340")) {
                                                                                                                                                                                            return "0";
                                                                                                                                                                                        } else if (!field1.equals("4340")) {
                                                                                                                                                                                            if (field1.equals("2450")) {
                                                                                                                                                                                                return "7000000";
                                                                                                                                                                                            } else if (!field1.equals("2450")) {
                                                                                                                                                                                                if (field4.equals("8450")) {
                                                                                                                                                                                                    return "25000000";
                                                                                                                                                                                                } else if (!field4.equals("8450")) {
                                                                                                                                                                                                    if (field1.equals("7738")) {
                                                                                                                                                                                                        return "1000000";
                                                                                                                                                                                                    } else if (!field1.equals("7738")) {
                                                                                                                                                                                                        return "6000000";
                                                                                                                                                                                                    }
                                                                                                                                                                                                }
                                                                                                                                                                                            }
                                                                                                                                                                                        }
                                                                                                                                                                                    }
                                                                                                                                                                                }
                                                                                                                                                                            }
                                                                                                                                                                        } else if (!field5.equals("0")) {
                                                                                                                                                                            return "4000000";
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
                                                                                                                            } else if (field8 <= 347) {
                                                                                                                                return "12500000";
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
                                                            } else if (field8 <= 307) {
                                                                if (field8 > 271) {
                                                                    return "250000";
                                                                } else if (field8 <= 271) {
                                                                    return "2000000";
                                                                }
                                                            }
                                                        } else if (field8 <= 261) {
                                                            if (field4.equals("3371")) {
                                                                return "7000000";
                                                            } else if (!field4.equals("3371")) {
                                                                if (field2 == null) {
                                                                    return "8000000";
                                                                } else if (field2.equals("9045")) {
                                                                    return "75000000";
                                                                } else if (!field2.equals("9045")) {
                                                                    if (field8 > 151) {
                                                                        if (field5.equals("0")) {
                                                                            if (field4.equals("3327")) {
                                                                                return "40000000";
                                                                            } else if (!field4.equals("3327")) {
                                                                                if (field2.equals("0")) {
                                                                                    return "12500000";
                                                                                } else if (!field2.equals("0")) {
                                                                                    if (field2.equals("4503")) {
                                                                                        return "8000000";
                                                                                    } else if (!field2.equals("4503")) {
                                                                                        if (field1.equals("8661")) {
                                                                                            return "20000000";
                                                                                        } else if (!field1.equals("8661")) {
                                                                                            if (field3.equals("7432")) {
                                                                                                return "30000000";
                                                                                            } else if (!field3.equals("7432")) {
                                                                                                if (field8 > 196) {
                                                                                                    if (field3.equals("0")) {
                                                                                                        return "12500000";
                                                                                                    } else if (!field3.equals("0")) {
                                                                                                        return "25000000";
                                                                                                    }
                                                                                                } else if (field8 <= 196) {
                                                                                                    if (field8 > 175) {
                                                                                                        return "8000000";
                                                                                                    } else if (field8 <= 175) {
                                                                                                        return "7000000";
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        } else if (!field5.equals("0")) {
                                                                            if (field8 > 203) {
                                                                                return "12500000";
                                                                            } else if (field8 <= 203) {
                                                                                return "4000000";
                                                                            }
                                                                        }
                                                                    } else if (field8 <= 151) {
                                                                        if (field1.equals("4656")) {
                                                                            return "5000000";
                                                                        } else if (!field1.equals("4656")) {
                                                                            if (field1.equals("4340")) {
                                                                                return "6000000";
                                                                            } else if (!field1.equals("4340")) {
                                                                                if (field4.equals("0")) {
                                                                                    return "1000000";
                                                                                } else if (!field4.equals("0")) {
                                                                                    return "3500000";
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
            } else if (field8 <= 127) {
                if (field2 == null) {
                    return "250000";
                } else if (field2.equals("8667")) {
                    if (field8 > 75) {
                        if (field8 > 105) {
                            return "4500000";
                        } else if (field8 <= 105) {
                            if (field3 == null) {
                                return "1000000";
                            } else if (field3.equals("0")) {
                                return "1000000";
                            } else if (!field3.equals("0")) {
                                return "2000000";
                            }
                        }
                    } else if (field8 <= 75) {
                        return "250000";
                    }
                } else if (!field2.equals("8667")) {
                    if (field8 > 58) {
                        if (field1.equals("4340")) {
                            if (field5 == null) {
                                return "1500000";
                            } else if (field5.equals("4450")) {
                                return "12500000";
                            } else if (!field5.equals("4450")) {
                                if (field8 > 66) {
                                    if (field8 > 116) {
                                        return "250000";
                                    } else if (field8 <= 116) {
                                        if (field3 == null) {
                                            return "1500000";
                                        } else if (field3.equals("4504")) {
                                            return "8000000";
                                        } else if (!field3.equals("4504")) {
                                            if (field4 == null) {
                                                return "1500000";
                                            } else if (field4.equals("4444")) {
                                                return "10000000";
                                            } else if (!field4.equals("4444")) {
                                                return "1500000";
                                            }
                                        }
                                    }
                                } else if (field8 <= 66) {
                                    if (field2.equals("4382")) {
                                        return "250000";
                                    } else if (!field2.equals("4382")) {
                                        return "1000000";
                                    }
                                }
                            }
                        } else if (!field1.equals("4340")) {
                            if (field2.equals("2677")) {
                                if (field3 == null) {
                                    return "25000000";
                                } else if (field3.equals("2678")) {
                                    return "25000000";
                                } else if (!field3.equals("2678")) {
                                    if (field8 > 87) {
                                        return "6000000";
                                    } else if (field8 <= 87) {
                                        return "2000000";
                                    }
                                }
                            } else if (!field2.equals("2677")) {
                                if (field1.equals("3317")) {
                                    if (field6 == null) {
                                        return "7000000";
                                    } else if (field6.equals("0")) {
                                        if (field8 > 104) {
                                            return "6000000";
                                        } else if (field8 <= 104) {
                                            return "1000000";
                                        }
                                    } else if (!field6.equals("0")) {
                                        return "7000000";
                                    }
                                } else if (!field1.equals("3317")) {
                                    if (field3 == null) {
                                        return "1000000";
                                    } else if (field3.equals("2568")) {
                                        return "5000000";
                                    } else if (!field3.equals("2568")) {
                                        if (field1.equals("4656")) {
                                            return "1000000";
                                        } else if (!field1.equals("4656")) {
                                            if (field1.equals("7365")) {
                                                return "3500000";
                                            } else if (!field1.equals("7365")) {
                                                if (field2.equals("7314")) {
                                                    return "3500000";
                                                } else if (!field2.equals("7314")) {
                                                    if (field1.equals("5128")) {
                                                        if (field2.equals("0")) {
                                                            return "2000000";
                                                        } else if (!field2.equals("0")) {
                                                            return "1000000";
                                                        }
                                                    } else if (!field1.equals("5128")) {
                                                        if (field3.equals("1954")) {
                                                            return "12500000";
                                                        } else if (!field3.equals("1954")) {
                                                            if (field8 > 94) {
                                                                if (field1.equals("950")) {
                                                                    return "3500000";
                                                                } else if (!field1.equals("950")) {
                                                                    if (field1.equals("2450")) {
                                                                        return "50000000";
                                                                    } else if (!field1.equals("2450")) {
                                                                        return "1000000";
                                                                    }
                                                                }
                                                            } else if (field8 <= 94) {
                                                                if (field5 == null) {
                                                                    return "1000000";
                                                                } else if (field5.equals("0")) {
                                                                    if (field1.equals("8259")) {
                                                                        return "250000";
                                                                    } else if (!field1.equals("8259")) {
                                                                        if (field2.equals("8076")) {
                                                                            return "250000";
                                                                        } else if (!field2.equals("8076")) {
                                                                            if (field2.equals("3786")) {
                                                                                return "40000000";
                                                                            } else if (!field2.equals("3786")) {
                                                                                if (field2.equals("2371")) {
                                                                                    return "250000";
                                                                                } else if (!field2.equals("2371")) {
                                                                                    if (field2.equals("2451")) {
                                                                                        return "3000000";
                                                                                    } else if (!field2.equals("2451")) {
                                                                                        if (field4 == null) {
                                                                                            return "1000000";
                                                                                        } else if (field4.equals("0")) {
                                                                                            return "1000000";
                                                                                        } else if (!field4.equals("0")) {
                                                                                            return "1000000";
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                } else if (!field5.equals("0")) {
                                                                    return "1000000";
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
                    } else if (field8 <= 58) {
                        if (field3 == null) {
                            return "250000";
                        } else if (field3.equals("2678")) {
                            return "8000000";
                        } else if (!field3.equals("2678")) {
                            if (field2.equals("3318")) {
                                return "1000000";
                            } else if (!field2.equals("3318")) {
                                if (field1.equals("4340")) {
                                    if (field3.equals("4439")) {
                                        return "2000000";
                                    } else if (!field3.equals("4439")) {
                                        return "250000";
                                    }
                                } else if (!field1.equals("4340")) {
                                    if (field3.equals("0")) {
                                        if (field8 > 53) {
                                            return "100000000";
                                        } else if (field8 <= 53) {
                                            if (field8 > 29) {
                                                return "250000";
                                            } else if (field8 <= 29) {
                                                return "250000";
                                            }
                                        }
                                    } else if (!field3.equals("0")) {
                                        if (field1.equals("2450")) {
                                            return "250000";
                                        } else if (!field1.equals("2450")) {
                                            if (field4 == null) {
                                                return "250000";
                                            } else if (field4.equals("0")) {
                                                return "250000";
                                            } else if (!field4.equals("0")) {
                                                if (field8 > 35) {
                                                    return "250000";
                                                } else if (field8 <= 35) {
                                                    if (field8 > 29) {
                                                        return "1500000";
                                                    } else if (field8 <= 29) {
                                                        return "1000000";
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
