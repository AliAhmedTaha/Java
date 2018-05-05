package os_ii_3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

public class OS_II_3 {

    public static class Block {

        public boolean State = false;
        public String Name = "";
    }

    public static class File {

        String Name = "";
        public Vector<Integer> IndexBlock = new Vector();

        public File(String N, Vector<Integer> Ind) {
            IndexBlock = Ind;
            Name = N;
        }

    }

    public static class Dirictory {

        String Name = "";
        public Vector<Dirictory> Folders = new Vector();
        public Vector<File> Files = new Vector();

        public Dirictory(String N) {
            this.Name = N;
        }
    }

    public static Vector<Block> Initilize(int Size) {
        Vector<Block> Disk = new Vector();
        Block Temp;
        for (int I = 0; I < Size; I++) {
            Temp = new Block();
            Disk.add(Temp);
        }
        return Disk;
    }

    public static void Load(Vector<Block> Disk, Dirictory Data) {
        try {
            BufferedReader Read = new BufferedReader(new FileReader("VFS.txt"));
            String Temp = "", File = "";
            String Pathes[], Spliter[], Blocks[];
            while ((Temp = Read.readLine()) != null) {
                File += Temp;
            }
            Pathes = File.split("=");
            for (int I = 0; I < Pathes.length; I++) {
                Spliter = Pathes[I].split(" ");
                if (Spliter.length == 1) {
                    CreateFolder(Spliter[0], Data);
                } else {
                    Blocks = Spliter[1].split(",");
                    Vector<Integer> Allocated = new Vector();
                    for (int It = 1; It < Blocks.length; It++) {
                        Allocated.add(Integer.parseInt(Blocks[It]));
                    }
                    Allocate(Spliter[0], Allocated, Disk);
                    CreateFile(Spliter[0], 0,Allocated, Disk, Data);
                }
            }
        } catch (Exception Ex) {

        }
    }

    public static void Save(Dirictory Data) {
        Stack<Dirictory> Container = new Stack();
        Dirictory Temp;
        try {
            PrintWriter Write = new PrintWriter("VFS.txt");
            Container.push(Data);
            while (!Container.empty()) {
                Temp = Container.peek();
                Container.pop();
                Write.print("=" + Temp.Name);
                for (int I = 0; I < Temp.Files.size(); I++) {
                    Write.print("=" + Temp.Files.elementAt(I).Name + " " + String.valueOf(Temp.Files.elementAt(I).IndexBlock.size()) + ",");
                    for (int It = 0; It < Temp.Files.elementAt(I).IndexBlock.size(); It++) {
                        Write.print(Temp.Files.elementAt(I).IndexBlock.elementAt(It) + ",");
                    }
                }
                for (int I = 0; I < Temp.Folders.size(); I++) {
                    Container.push(Temp.Folders.elementAt(I));
                }
            }
            Write.close();
        } catch (Exception Ex) {
        }
    }

    public static void PrintSpaces(int Num) {
        for (int I = 0; I < Num; I++) {
            System.out.print("    ");
        }
    }

    public static void Check(boolean Result) {
        if (!Result) {
            System.err.println("Wrong Path!");
        }
    }

    public static String GetName(String Path) {
        String Split[] = Path.split("/");
        return Split[Split.length - 1];
    }

    public static int GetFree(Vector<Block> Disk) {
        int Counter = 0;
        for (int I = 0; I < Disk.size(); I++) {
            if (!Disk.elementAt(I).State) {
                Counter++;
            }
        }
        return Counter;
    }

    public static int GetOccupied(Vector<Block> Disk) {
        int Counter = 0;
        for (int I = 0; I < Disk.size(); I++) {
            if (Disk.elementAt(I).State) {
                Counter++;
            }
        }
        return Counter;
    }

    public static Vector<Integer> Allocate(String File, int Size, Vector<Block> Disk) {
        Vector<Integer> Index = new Vector();
        for (int I = 0; I < Disk.size() && Size != 0; I++) {
            if (!Disk.elementAt(I).State) {
                Disk.elementAt(I).State = true;
                Disk.elementAt(I).Name = File;
                Index.add(I);
                Size--;
            }
        }
        return Index;
    }

    public static void Allocate(String File, Vector<Integer> Blocks, Vector<Block> Disk) {
        for (int I = 0; I < Blocks.size(); I++) {
            Disk.elementAt(Blocks.elementAt(I)).State = true;
            Disk.elementAt(Blocks.elementAt(I)).Name = File;
        }
    }

    public static void Deallocate(Vector<Integer> File, Vector<Block> Disk) {
        for (int I = 0; I < File.size(); I++) {
            Disk.elementAt(File.elementAt(I)).State = false;
            Disk.elementAt(I).Name = "";
        }
    }

    public static boolean CreateFile(String Path, int Size, Vector<Integer> Allocated, Vector<Block> Disk, Dirictory Data) {

        String Split[] = Path.split("/");
        if (Split[0].equals("root")) {
            Vector<Dirictory> Curr;
            Vector<Integer> IndexTable;
            Curr = Data.Folders;
            int Ind = 1;
            if (Split.length == 2) {
                for (int It = 0; It < Data.Files.size(); It++) {
                    if (GetName(Data.Files.elementAt(It).Name).equals(Split[Split.length - 1])) {
                        System.err.println("File exists!");
                        return false;
                    }
                }
                if (Size != 0) {
                    IndexTable = Allocate(Path, Size, Disk);
                } else {
                    IndexTable = Allocated;
                }
                File Temp = new File(Path, IndexTable);
                Data.Files.add(Temp);
                return true;
            } else {
                for (int I = 0; I < Curr.size(); I++) {
                    if (GetName(Curr.elementAt(I).Name).equals(Split[Ind])) {
                        if (Ind == Split.length - 2) {
                            for (int It = 0; It < Curr.elementAt(I).Files.size(); It++) {
                                if (GetName(Curr.elementAt(I).Files.elementAt(It).Name).equals(Split[Split.length - 1])) {
                                    System.err.println("File exists!");
                                    return false;
                                }
                            }
                            if (Size != 0) {
                                IndexTable = Allocate(Path, Size, Disk);
                            } else {
                                IndexTable = Allocated;
                            }
                            File Temp = new File(Path, IndexTable);
                            Curr.elementAt(I).Files.add(Temp);
                            return true;
                        } else {
                            Curr = Curr.elementAt(I).Folders;
                            Ind++;
                            I = -1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean CreateFolder(String Path, Dirictory Data) {
        String Split[] = Path.split("/");
        if (Split[0].equals("root")) {
            Vector<Dirictory> Curr;
            Curr = Data.Folders;
            int Ind = 1;
            if (Split.length == 2) {
                for (int It = 0; It < Curr.size(); It++) {
                    if (GetName(Curr.elementAt(It).Name).equals(Split[Split.length - 1])) {
                        System.err.println("Folder exists!");
                        return false;
                    }
                }
                Dirictory Temp = new Dirictory(Path);
                Curr.add(Temp);
                return true;
            } else {
                for (int I = 0; I < Curr.size(); I++) {
                    if (GetName(Curr.elementAt(I).Name).equals(Split[Ind])) {
                        Curr = Curr.elementAt(I).Folders;
                        if (Ind == Split.length - 2) {
                            for (int It = 0; It < Curr.size(); It++) {
                                if (GetName(Curr.elementAt(It).Name).equals(Split[Split.length - 1])) {
                                    System.err.println("Folder exists!");
                                    return false;
                                }
                            }
                            Dirictory Temp = new Dirictory(Path);
                            Curr.add(Temp);
                            return true;
                        } else {
                            Ind++;
                            I = -1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean DeleteFile(String Path, Vector<Block> Disk, Dirictory Data) {

        String Split[] = Path.split("/");
        if (Split[0].equals("root")) {
            Vector<Dirictory> Curr;
            Curr = Data.Folders;
            int Ind = 1;
            if (Split.length == 2) {
                for (int It = 0; It < Data.Files.size(); It++) {
                    if (GetName(Data.Files.elementAt(It).Name).equals(Split[Split.length - 1])) {
                        Deallocate(Data.Files.elementAt(It).IndexBlock, Disk);
                        Data.Files.removeElementAt(It);
                        return true;
                    }
                }
            } else {
                for (int I = 0; I < Curr.size(); I++) {
                    if (GetName(Curr.elementAt(I).Name).equals(Split[Ind])) {
                        if (Ind == Split.length - 2) {
                            for (int It = 0; It < Curr.elementAt(I).Files.size(); It++) {
                                if (GetName(Curr.elementAt(I).Files.elementAt(It).Name).equals(Split[Split.length - 1])) {
                                    Deallocate(Curr.elementAt(I).Files.elementAt(It).IndexBlock, Disk);
                                    Curr.elementAt(I).Files.removeElementAt(It);
                                    return true;
                                }
                            }
                        } else {
                            Curr = Curr.elementAt(I).Folders;
                            Ind++;
                            I = -1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean DeleteFolder(String Path, Vector<Block> Disk, Dirictory Data) {
        String Split[] = Path.split("/");
        if (Split[0].equals("root")) {
            Vector<Dirictory> Curr;
            Curr = Data.Folders;
            int Ind = 1;
            if (Split.length == 2) {
                for (int It = 0; It < Curr.size(); It++) {
                    if (GetName(Curr.elementAt(It).Name).equals(Split[Split.length - 1])) {
                        DeleteSubFiles(Disk, Curr.elementAt(It), Data);
                        Curr.removeElementAt(It);
                        return true;
                    }
                }
            } else {
                for (int I = 0; I < Curr.size(); I++) {
                    if (GetName(Curr.elementAt(I).Name).equals(Split[Ind])) {
                        Curr = Curr.elementAt(I).Folders;
                        if (Ind == Split.length - 2) {
                            for (int It = 0; It < Curr.size(); It++) {
                                if (GetName(Curr.elementAt(It).Name).equals(Split[Split.length - 1])) {
                                    Curr.removeElementAt(It);
                                    return true;
                                }
                            }
                            return false;
                        } else {
                            Ind++;
                            I = -1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean DeleteSubFiles(Vector<Block> Disk, Dirictory Data, Dirictory Root) {
        Stack<Dirictory> Container = new Stack();
        Dirictory Temp;
        Container.push(Data);
        while (!Container.empty()) {
            Temp = Container.peek();
            Container.pop();
            for (int I = 0; I < Temp.Files.size(); I++) {
                DeleteFile(Temp.Files.elementAt(I).Name, Disk, Root);
            }
            for (int I = 0; I < Temp.Folders.size(); I++) {
                Container.push(Temp.Folders.elementAt(I));
            }
        }
        return false;
    }

    public static void DisplayDiskStatus(Vector<Block> Disk) {
        System.out.println("1" + GetOccupied(Disk) + " " + "0" + GetFree(Disk));
        for (int I = 0; I < Disk.size(); I++) {
            if (Disk.elementAt(I).State) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
        }
        System.out.println();
    }

    public static void DisplayDiskStructure(Dirictory Root) {
        Stack<Dirictory> Container = new Stack();
        Dirictory Temp;
        String Spliter[];
        Container.push(Root);
        while (!Container.empty()) {
            Temp = Container.peek();
            Container.pop();
            Spliter = Temp.Name.split("/");
            PrintSpaces(Spliter.length - 1);
            System.out.println("♦ <" + GetName(Temp.Name) + ">");
            for (int I = 0; I < Temp.Files.size(); I++) {
                Spliter = Temp.Files.elementAt(I).Name.split("/");
                PrintSpaces(Spliter.length - 1);
                System.out.println("• " + GetName(Temp.Files.elementAt(I).Name));
            }
            for (int I = 0; I < Temp.Folders.size(); I++) {
                Container.push(Temp.Folders.elementAt(I));
            }
        }
    }

    public static void main(String[] args) {

        boolean Good = true;
        String Input, Command[];
        Scanner In = new Scanner(System.in);
        Vector<Block> DiskBlocks;
        Dirictory Root = new Dirictory("root");
        DiskBlocks = Initilize(50);
        Load(DiskBlocks, Root);

        while (Good) {
            System.out.print("Enter a Command: ");
            Input = In.nextLine();
            Command = Input.split(" ");
            switch (Command[0]) {
                case "CreateFolder":
                    Check(CreateFolder(Command[1], Root));
                    break;
                case "CreateFile":
                    if (Command.length == 3 && GetFree(DiskBlocks) >= Integer.valueOf(Command[2])) {
                        Check(CreateFile(Command[1], Integer.valueOf(Command[2]), null, DiskBlocks, Root));
                    } else {
                        System.err.println("There is no enough space!");
                    }
                    break;
                case "DeleteFolder":
                    Check(DeleteFolder(Command[1], DiskBlocks, Root));
                    break;
                case "DeleteFile":
                    Check(DeleteFile(Command[1], DiskBlocks, Root));
                    break;
                case "DisplayDiskStatus":
                    DisplayDiskStatus(DiskBlocks);
                    break;
                case "DisplayDiskStructure":
                    DisplayDiskStructure(Root);
                    break;
                case "Exit":
                    Save(Root);
                    Good = false;
                    break;
                default:
                    System.err.println("Wrong Input!!");
            }
        }
    }
}

