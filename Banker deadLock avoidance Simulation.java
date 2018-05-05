package deadlock_avoidance;

import static java.lang.System.out;
import java.util.Scanner;

public class DeadLock_Avoidance {

    public static void Done(int NOfP, int NOfR, int All[][], int Ned[][], int Ava[]) {
        for (int I = 0; I < NOfR; I++) {
            Ava[I] += All[NOfP][I];
            All[NOfP][I] = 0;
            Ned[NOfP][I] = 0;
        }
    }

    public static void Initialize(boolean Arr[]) {
        for (int I = 0; I < Arr.length; I++) {
            Arr[I] = false;
        }
    }

    public static void Reset(int Org[][], int Cpy[][]) {
        for (int I = 0; I < Org.length; I++) {
            for (int It = 0; It < Org[0].length; It++) {
                Cpy[I][It] = Org[I][It];
            }
        }
    }

    public static void Reset(int Org[], int Cpy[]) {
        for (int I = 0; I < Org.length; I++) {
            Cpy[I] = Org[I];
        }
    }

    public static void View(char Type, int Arr[][]) {
        for (int I = 0; I < Arr.length; I++) {
            if (I == 0) {
                out.print(Type + "  ");
                for (int It = 0; It < Arr[0].length; It++) {
                    out.print("R" + It + "  ");
                }
                out.println();
            }
            for (int It = 0; It < Arr[0].length; It++) {
                if (It == 0) {
                    out.print("P" + I + " ");
                }
                out.print(Arr[I][It] + "   ");
            }
            out.println();
        }
        out.println("-------------");

    }

    public static void View(int Arr[]) {
        out.print("\n\nAv ");
        for (int I = 0; I < Arr.length; I++) {
            out.print("R" + I + "  ");
        }
        out.println();
        for (int I = 0; I < Arr.length; I++) {
            if (I == 0) {
                out.print("   ");
            }
            out.print(Arr[I] + "   ");
        }
        out.println("\n-------------");
    }

    public static void main(String[] args) {

        int NumOfProcesses, NumOfResources;
        Scanner In = new Scanner(System.in);

        out.print("Enter number of Resources:");
        NumOfResources = In.nextInt();
        int Available[] = new int[NumOfResources];
        int CAvailable[] = new int[NumOfResources];

        out.print("Enter available instances of each resource from R0 to R" + (NumOfResources - 1) + ":");
        for (int I = 0; I < NumOfResources; I++) {
            In = new Scanner(System.in);
            Available[I] = In.nextInt();
        }

        out.print("Enter number of Processes:");
        In = new Scanner(System.in);
        NumOfProcesses = In.nextInt();

        int Max[][] = new int[NumOfProcesses][NumOfResources];
        int Allocated[][] = new int[NumOfProcesses][NumOfResources];
        int Needed[][] = new int[NumOfProcesses][NumOfResources];
        int CMax[][] = new int[NumOfProcesses][NumOfResources];
        int CAllocated[][] = new int[NumOfProcesses][NumOfResources];
        int CNeeded[][] = new int[NumOfProcesses][NumOfResources];
        boolean Visited[] = new boolean[NumOfProcesses];

        for (int I = 0; I < NumOfProcesses; I++) {
            out.print("Enter Max needs of P[" + I + "] from R0 to R" + (NumOfResources - 1) + ":");
            for (int It = 0; It < NumOfResources; It++) {
                In = new Scanner(System.in);
                Max[I][It] = In.nextInt();
            }
        }

        for (int I = 0; I < NumOfProcesses; I++) {
            out.print("Enter allocated instances to P[" + I + "] from R0 to R" + (NumOfResources - 1) + ":");
            for (int It = 0; It < NumOfResources; It++) {
                In = new Scanner(System.in);
                Allocated[I][It] = In.nextInt();
            }
        }

        for (int I = 0; I < NumOfProcesses; I++) {
            for (int It = 0; It < NumOfResources; It++) {
                Needed[I][It] = Max[I][It] - Allocated[I][It];
            }
        }

        boolean Flag = true;
        String Input, Split[];
        while (Flag) {
            Reset(Available, CAvailable);
            Reset(Allocated, CAllocated);
            Reset(Needed, CNeeded);
            Reset(Max, CMax);
            Initialize(Visited);
            View(Available);
            View('M', Max);
            View('A', Allocated);
            View('N', Needed);

            out.print("Enter your operation: ");
            In = new Scanner(System.in);
            Input = In.nextLine();
            Split = Input.split(" ");

            switch (Split[0]) {
                case "RQ":
                    if (Split.length != NumOfResources + 2) {
                        out.println("Wrong Input!");
                    } else {
                        int Pnum = Character.getNumericValue(Split[1].charAt(1)), Test;
                        boolean Check = true;
                        for (int I = 0; I < NumOfResources; I++) {
                            Test = Integer.parseInt(Split[I + 2]);
                            if (Test <= Needed[Pnum][I] && Test <= Available[I]) {
                                continue;
                            } else {
                                out.println("Request can’t be Granted!");
                                Check = false;
                                break;
                            }
                        }
                        if (Check) {
                            for (int I = 0; I < NumOfResources; I++) {
                                Test = Integer.parseInt(Split[I + 2]);
                                CAllocated[Pnum][I] += Test;
                                CNeeded[Pnum][I] -= Test;
                                CAvailable[I] -= Test;
                            }
                            View(CAvailable);
                            View('M', CMax);
                            View('A', CAllocated);
                            View('N', CNeeded);
                            int Counter = 0, Sign, Itr = 0;
                            String Sequence = "\n";
                            while (true) {
                                if (Counter == NumOfProcesses) {
                                    out.println(Sequence + "..Safe State [^_^]\n\n");
                                    for (int I = 0; I < NumOfResources; I++) {
                                        Test = Integer.parseInt(Split[I + 2]);
                                        Allocated[Pnum][I] += Test;
                                        Needed[Pnum][I] -= Test;
                                        Available[I] -= Test;
                                    }
                                    break;
                                } else if (Itr == NumOfProcesses) {
                                    Sequence += "DeadLock ";
                                    out.println(Sequence + "..UnSafe State [-_-]\n\n");
                                    break;
                                }
                                for (int I = 0; I < NumOfProcesses; I++) {
                                    Sign = 0;
                                    if (!Visited[I]) {
                                        for (int It = 0; It < NumOfResources; It++) {
                                            if (CNeeded[I][It] <= CAvailable[It]) {
                                                Sign++;
                                            }
                                            if (Sign == NumOfResources) {
                                                Visited[I] = true;
                                                Counter++;
                                                Itr = 0;
                                                Sequence = Sequence + "P" + String.valueOf(I) + " ";
                                                Done(I, NumOfResources, CAllocated, CNeeded, CAvailable);
                                                View(CAvailable);
                                                View('M', CMax);
                                                View('A', CAllocated);
                                                View('N', CNeeded);
                                            }
                                            if (It == NumOfResources - 1) {
                                                Itr++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "Quit":
                    Flag = false;
                    break;
                default:
                    out.println("Wrong Input!");
            }
        }

    }
}

