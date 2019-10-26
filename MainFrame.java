package level3;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.StringTokenizer;
public class MainFrame extends JFrame{
		
	    String[] UcoTable = {"Line", "Label","Source","val1","val2","val3"}; // UcodeTable ����
	    String[] SymTable = {"Label", "Address"}; // LabelTable ����
	    Assemble assemble = new Assemble(this); // Assemble ��ü ����
	    String[] Command = // ��ɾ� �迭, 37��
		 	{"nop", "bgn", "sym",  // ���α׷� ���� ���
		 	 "lod", "lda", "ldc", "str", "ldi", "sti", // ������ �̵� ������
		 	 "not", "neg", "inc", "dec", "dup", // ���� ������
		 	 "add", "sub", "mult", "div", "mod", "gt", "lt", "ge", "le", "eq", "ne", "and", "or", "swp", // ���� ������
		 	 "ujp", "tjp", "fjp", // �帧����
		 	 "call", "ret", "ldp", "push", "proc", "end", // �Լ� ���� �� ȣ��     
		 	};
	    SymbolTable_[] label_info; // �� ���� ��ü �迭
		CodeTable_[] asm_info; // ��ɾ� ���� ��ü �迭
	   
		int[] CommandCnt; // ��ɾ� ���� Ƚ�� �迭
		int[] Memory; // ���� �޸�
		int MemoryApr; // �޸� ���� Ƚ��
		int StackApr; // ���� ���� Ƚ��
		int cycle; // �� ��ɾ� ���� Ƚ��
		int index_label; //�ݺ������� nullpoint������ ���� �ε���
		int regBP; // �޸� bp
		int regSP; // �޸� sp
		int regPC; // �޸� pc
		int maxLine; // ������ ��		
		int LoadTrue = 0; // ���� �ε� ����
		File resultFile; // ��� ����
		
	    /*���ȭ�� "Line", "Label","Source","val1","val2","val3"���̺�� "Symbol", "Index"���̺�*/
		public DefaultTableModel Uco= new DefaultTableModel(UcoTable,0);// UcodeTable ���� ����
		public DefaultTableModel Symbol= new DefaultTableModel(SymTable, 0); // LabelTable ���� ����
		public JTable CodeTable = new JTable(Uco); // Ucode ��� Table
		public JTable SymbolTable = new JTable(Symbol); // Label ��� Table
		JScrollPane Codescroll = new JScrollPane(CodeTable); // CodeTable Scroll
		JScrollPane SymbolScroll = new JScrollPane(SymbolTable); // LabelTable Scroll
		
		/*����� ���� �ؽ�Ʈ������*/
		JTextArea StackText = new JTextArea("-Stack-"); // ���� ��� �ؽ�Ʈ
		JTextArea MemoryText = new JTextArea("-Memory-"); // �޸� ��� �ؽ�Ʈ
		JTextArea ListText = new JTextArea("-List-"); // ����Ʈ ��� �ؽ�Ʈ
		/*JScrollPane�� ������ ������� ��ũ���� �������*/
		JScrollPane ListScroll = new JScrollPane(ListText); // ListText Scroll
		JScrollPane MemScroll = new JScrollPane(MemoryText);
		
		/*���� �Է��� ���� ������*/
		String[] UcoName = {"factorial.uco","prime.uco","sigma.uco"};
		JComboBox LoadCom = new JComboBox(UcoName);
		String FileName;
		
		/*������ ���� ��ư��*/
		JButton LoadBtn = new JButton("Load"); // uco������ �޾ƿ� ��ư
		JButton StepBtn = new JButton("Step"); // �ܰ躰�� uco���� ���� ��ų ��ư
		JButton RunBtn = new JButton("Run");	// ���������� �����ų ��ư
		JButton ListBtn = new JButton("Create lst"); // List ��� ��ư
		JButton FinishBtn = new JButton("Ȯ��"); // Ȯ�� �� ���� ��ư
	   
		JFrame JF = new JFrame();	 // ��ü Ʋ
		JPanel JP1 = new JPanel();   // uco �޸�  ����  �ɺ����̺� ����� JP3
		JPanel JP2 = new JPanel();   // List
		JPanel JP3 = new JPanel();   // JP4 JP5
		JPanel JP4 = new JPanel();   // Symbol
		JPanel JP5 = new JPanel();   // �޺��ڽ� ��ư��
		
		
		
	public MainFrame(){
			cycle=0; // �� ��ɾ� ���� Ƚ�� �ʱ�ȭ
			MemoryApr=0; // �޸� ���� Ƚ�� �ʱ�ȭ
			StackApr=0; // ���� ���� Ƚ�� �ʱ�ȭ
			regPC=0;  // �޸� pc �ʱ�ȭ
			regSP=-1; // �޸� sp �ʱ�ȭ
			regBP=-1; // �޸� bp �ʱ�ȭ
			maxLine=0; // ������ �� �ʱ�ȭ
			Memory = new int[1000]; //�޸� ���� 1000��
			CommandCnt = new int[37]; //��ɾ� 37���� ���� ��ɾ� ���� Ƚ��
			label_info = new SymbolTable_[30]; //�� �ִ� 30��
			asm_info = new CodeTable_[300];    //��ɾ� ���� ���� 300�� 
			
			JF.setTitle("U-code Interpreter"); // ����
			JF.setDefaultCloseOperation(EXIT_ON_CLOSE);
			setLayout(null);
		   
			/*���̾ƿ� ���� �� ������Ʈ ����*/
			JF.setLayout(new GridLayout(2,1,10,10));
			JP1.setLayout(new GridLayout(1,4,20,20));
			JP2.setLayout(new GridLayout(1,1,30,30));
			JP3.setLayout(new GridLayout(2,1,10,10));
			JP4.setLayout(new GridLayout(1,1,10,10));
			JP5.setLayout(new GridLayout(6,1,10,10));
			
			JF.add(JP1);
			JF.add(JP2);
			
			JP1.add(Codescroll);
			JP1.add(MemScroll);
			JP1.add(StackText);
			JP1.add(JP3);
			
			JP2.add(ListScroll);
			
			JP3.add(JP4);
			JP3.add(JP5);
			
			JP4.add(SymbolScroll);
			
			JP5.add(LoadCom);
			JP5.add(LoadBtn);
			JP5.add(StepBtn);
			JP5.add(RunBtn);
			JP5.add(ListBtn);
			JP5.add(FinishBtn);
		   
			JF.setSize(1200,1035);
		    JF.setVisible(true);
		    
		    
		 
		    LoadBtn.addActionListener // Load ��ư �̺�Ʈ
		    (
		 			new ActionListener() 
	 				{
 						public void actionPerformed(ActionEvent e)
						{
 							/*������ ���ι��� �� ���� �ʱ�ȭ*/
 							LoadTrue = 1;
 							MemoryText.setText("-Memory-"); // Mem TextArea �ʱ�ȭ
 							StackText.setText("-Stack-");
							ListText.setText("-List-"); // List TextArea �ʱ�ȭ
							//ResultText.setText("-Result-"); // Result TextArea �ʱ�ȭ
							Uco.setNumRows(0);  // Ucode Table Ŀ�� �ʱ�ȭ
							Symbol.setNumRows(0); // Label Table Ŀ�� �ʱ�ȭ
							cycle=0; // �� ��ɾ� ���� Ƚ�� �ʱ�ȭ
							MemoryApr=0; // �޸� ���� Ƚ�� �ʱ�ȭ
							StackApr=0; // ���� ���� Ƚ�� �ʱ�ȭ
							regPC=0;  // �޸� pc �ʱ�ȭ
							regSP=-1; // �޸� sp �ʱ�ȭ
							regBP=-1; // �޸� bp �ʱ�ȭ
							maxLine=0; // maxLine �ʱ�ȭ
							Memory = new int[1000]; //�޸� ���� 1000��
							assemble.ResultString="";
							for(int i=0;i<70;i++)
							{
								Memory[i]=-9999;	
							}
							CommandCnt = new int[37]; //��ɾ� 37���� ���� ��ɾ� ���� Ƚ��
							label_info = new SymbolTable_[30];
							asm_info = new CodeTable_[300];
							
							// ���� �Է��� ���� ������
							FileName = "C:\\\\Users\\\\����\\\\eclipse-workspace\\\\Test\\\\UCode �׽�Ʈ\\\\"
										+LoadCom.getSelectedItem().toString();
							File file = new File(FileName);
							resultFile = file;
							
							BufferedReader br = null; // ���ڸ� �о�帮�� ���� ���۸���
							int line = 0; // �� ���� ����ϱ� ���� ����
							int label_index = 0; // �� �迭�� �ε���

							try
							{
								FileReader fr = new FileReader(file); // ���Ͽ� ����� ����Ʈ�� �����ڵ� ���ڷ� ��ȯ�ؼ� �о��
								br = new BufferedReader(fr); //��� String���� �о����
								String s;
								String rest;
								
								while ((s = br.readLine()) != null)  //������ ������ ��� ���� ��ȯ
								{
									int indexof; // %�� ��ġ �ε� ���� ������ ���� ����
									indexof = s.indexOf("%"); // ���پ� ��ȯ�� ���ڿ����� %�� �ε����� ã��
									
									if (indexof!=-1) // %��� ���ڰ� ������
									{
										s = s.substring(0, indexof - 1); // S��  %���� �������� ���ڿ��� ����
									}
									String label = s.substring(0, 11);// ó�� 0~11��° �������� �� ���ڿ��� �Ѱ��� (0 ~ 10������ Label �ڸ��� ���)
									if (label.equals("           "))  //���� ������ �Ѿ
									{
										
									} 
									else //���� ������ ���� ������ ��ɾ� ��ġ�� ����
									{
										label = label.replaceAll(" ", ""); // ���� ����, " " -> "" ��ü
										label_info[label_index] = new SymbolTable_(label, line); // label_info ��ü�� ���� ����
										Symbol.addRow(new String[] {label,Integer.toString(line)}); //�󺧰���, �� �ε��� �� �߰�
										label_index++; //���� �����ϴ� �迭�� �ε����� ������Ŵ
									}
									rest = s.substring(11); // ���� �� ���ڿ�
									StringTokenizer str = new StringTokenizer(rest," "); // ���� ������ ����
									
									if(str.countTokens()==1)
									{
										String asm = str.nextToken();
										Uco.addRow(new String[] {Integer.toString(line), label,asm," "," "," "}); //��ɾ��� �� �߰�
										asm_info[line] = new CodeTable_(label, asm," "," "," "); // asm_info ��ü�� ���� ����
									}
									else if(str.countTokens()==2)
									{
										String asm = str.nextToken();
										String var1 = str.nextToken();
										Uco.addRow(new String[] {Integer.toString(line), label,asm,var1," "," "}); //��ɾ��� �� �߰�
										asm_info[line] = new CodeTable_(label, asm,var1," "," "); // asm_info ��ü�� ���� ����
									}
									else if(str.countTokens()==3)
									{
										String asm = str.nextToken();
										String var1 = str.nextToken();
										String var2 = str.nextToken();
										Uco.addRow(new String[] {Integer.toString(line), label,asm,var1,var2," "}); //��ɾ��� �� �߰�
										asm_info[line] = new CodeTable_(label, asm,var1,var2," "); // asm_info ��ü�� ���� ����
									}
									else if(str.countTokens()==4)
									{
										String asm = str.nextToken();
										String var1 = str.nextToken();
										String var2 = str.nextToken();
										String var3 = str.nextToken();
										Uco.addRow(new String[] {Integer.toString(line), label,asm,var1,var2,var3}); //��ɾ��� �� �߰�
										asm_info[line] = new CodeTable_(label, asm,var1,var2,var3); // asm_info ��ü�� ���� ����		 
									}

									line++;
								}
								br.close(); // ���� ��ȯ �� ���۸��� �ݱ�
								maxLine=line; // �������� ���� ����
								index_label = label_index; // 
								//ResultText.setText("-Result-");
							} catch (IOException e1) {
								
							}
							assemble.stack.clear(); // uco������ ���ιް� �����ϱ� ���� �����ʱ�ȭ
						}
					}
			);
		   
		    StepBtn.addActionListener // Step ��ư �̺�Ʈ
			(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							if(LoadTrue == 1)
							{
								if(regPC==maxLine+1) // end ��ɾ �������� �� ���� ���ܹ�ư�� ������ ���α׷� ���� ���
								{
									return;
								}
								CodeTable.changeSelection(regPC,1,false, false); // CodeTable�� ���� PC�� ��Ŀ�� �ֱ�
								
								assemble.runAssemble(asm_info[regPC]); // ��ɾ� ����, ���ڴ� �ش��ɾ� ���� //asm_info ��ü
								StackText.setText("-Stack-\n"); // ���� TextArea �ʱ�ȭ
								MemoryText.setText("-Memory"); // �޸� TextArea �ʱ�ȭ
								
								for (int i = 0; i < assemble.stack.size(); i++) //���þȿ� �׿��ִ� ���� ���
								{
						
									if(assemble.stack.get(i)>=1000000000 || assemble.stack.get(i)<=-1000000000)
									{
										StackText.append("OverFlow\n");
									}
									else
									{
										StackText.append(Integer.toString(assemble.stack.get(i)));
										StackText.append("\n");
									}
									
										
									
								}
								MemoryText.setText("-Memory-\n"+"regBP : "+regBP+"\n"+"regSP : "+regSP+"\n"); //���� bp�� sp ��ġ ���
								for(int i=0; i < 1000; i++) // �޸𸮰� ���
								{
									MemoryText.setCaretPosition(0); // Step��ư Ŭ���� ��ũ���� ������ ����
									MemoryText.append(i+"���� :"+Memory[i]);
									MemoryText.append("\n");
								}
								regPC++;
							}
							else {}
						}
					}
			);
		    RunBtn.addActionListener // Run ��ư �̺�Ʈ
			(
					new ActionListener() 
					{
						public void actionPerformed(ActionEvent e)
						{
							if(LoadTrue == 1)
							{
								if(regPC==maxLine+1) // end ��ɾ �������� �� ���� ���ܹ�ư�� ������ ���α׷� ���� ���
								{
									return;
								}
								while(regPC<=maxLine)//regPC�� ������ ���κ��� ���� ���� 
								{								
									
									assemble.runAssemble(asm_info[regPC]);	 //��ɾ� ����								
									regPC++;															
								}
								/*�� �޸� ��� ���*/
								MemoryText.setText("-Memory-\n"+"regBP : "+regBP+"\n"+"regSP : "+regSP+"\n"); //���� bp�� sp ��ġ ���
								for(int i=0; i < 1000; i++)  
								{
									MemoryText.setCaretPosition(0);
									MemoryText.append(i+"���� :"+Memory[i]);
									MemoryText.append("\n");
								}
							}
							else {}
							
						}
					}
			);
		   
		    ListBtn.addActionListener // List ��ư �̺�Ʈ
			(
					new ActionListener() 
					{
						public void actionPerformed(ActionEvent e)
						{
							if(LoadTrue == 1)
							{
								try {
									String input;
									FileReader fr = new FileReader(resultFile);
									BufferedReader br = null;
									br = new BufferedReader(fr);
		
									ListText.append("\n================================Assembling...================================\n");
									input = br.readLine();
									while (input != null)
									{
										ListText.append(input+"\n");
										input = br.readLine();
									}
									
									ListText.append("\n*******************************Result*****************************************\n");
									ListText.append(assemble.ResultString); // �����ʿ�
									ListText.append("\n###########################Statistics###########################\n");
									ListText.append("\n*************************Instruction  Count***********************************\n");
									for(int i=0; i<37; i++) // ��ɾ� ���� Ƚ�� ���
									{
										ListText.append(Command[i] +"    =    "+CommandCnt[i] +",  			 ");
										if((i+1)%4==0)
										{
											ListText.append("\n");
										}
									}
									ListText.append("\n\n Memory Approach Count : "+MemoryApr);
									ListText.append("\n\n Stack approach Count : "+StackApr);
									ListText.append("\n\n Total Cycle : "+cycle);
									String inDate = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
									ListText.append("\n\n---------------------------"+inDate+"---------------------------\n\n");
									// lst ���� �����
									
									if(LoadCom.getSelectedItem().toString().equals("factorial.uco"))
									{
										PrintWriter fw = new PrintWriter(new FileWriter(System.getProperty("user.dir")+"/FactorialResult.lst",true));
										fw.write(ListText.getText());
										fw.close();
									}
									else if(LoadCom.getSelectedItem().toString().equals("prime.uco"))
									{
										PrintWriter fw = new PrintWriter(new FileWriter(System.getProperty("user.dir")+"/PrimeResult.lst",true));
										fw.write(ListText.getText());
										fw.close();
									}
									else if(LoadCom.getSelectedItem().toString().equals("sigma.uco"))
									{
										PrintWriter fw = new PrintWriter(new FileWriter(System.getProperty("user.dir")+"/SigmaResult.lst",true));
										fw.write(ListText.getText());
										fw.close();
									}
									br.close();
									
								} catch (IOException e4) {
									
								}
							}
							else {}
						}						
					}
			);
		    FinishBtn.addActionListener  // Ȯ�� ��ư Ŭ���� ����
		    (
		    		new ActionListener() 
		    		{
		    			public void actionPerformed(ActionEvent e) 
		    			{
		    				System.exit(0);
		    			}
		    		}
		    );
	}
		    
	public static void main(String[] args) {
		// TODO �ڵ� ������ �޼ҵ� ����
		MainFrame frame = new MainFrame();
	}

}


