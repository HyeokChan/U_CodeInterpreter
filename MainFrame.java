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
		
	    String[] UcoTable = {"Line", "Label","Source","val1","val2","val3"}; // UcodeTable 제목
	    String[] SymTable = {"Label", "Address"}; // LabelTable 제목
	    Assemble assemble = new Assemble(this); // Assemble 객체 생성
	    String[] Command = // 명령어 배열, 37개
		 	{"nop", "bgn", "sym",  // 프로그램 구성 명령
		 	 "lod", "lda", "ldc", "str", "ldi", "sti", // 데이터 이동 연산자
		 	 "not", "neg", "inc", "dec", "dup", // 단항 연산자
		 	 "add", "sub", "mult", "div", "mod", "gt", "lt", "ge", "le", "eq", "ne", "and", "or", "swp", // 이항 연산자
		 	 "ujp", "tjp", "fjp", // 흐름제어
		 	 "call", "ret", "ldp", "push", "proc", "end", // 함수 정의 및 호출     
		 	};
	    SymbolTable_[] label_info; // 라벨 저장 객체 배열
		CodeTable_[] asm_info; // 명령어 저장 객체 배열
	   
		int[] CommandCnt; // 명령어 실행 횟수 배열
		int[] Memory; // 스택 메모리
		int MemoryApr; // 메모리 접근 횟수
		int StackApr; // 스택 접근 횟수
		int cycle; // 총 명령어 접근 횟수
		int index_label; //반복문에서 nullpoint방지를 위한 인덱스
		int regBP; // 메모리 bp
		int regSP; // 메모리 sp
		int regPC; // 메모리 pc
		int maxLine; // 마지막 줄		
		int LoadTrue = 0; // 파일 로드 여부
		File resultFile; // 결과 파일
		
	    /*출력화면 "Line", "Label","Source","val1","val2","val3"테이블과 "Symbol", "Index"테이블*/
		public DefaultTableModel Uco= new DefaultTableModel(UcoTable,0);// UcodeTable 제목 연결
		public DefaultTableModel Symbol= new DefaultTableModel(SymTable, 0); // LabelTable 제목 연결
		public JTable CodeTable = new JTable(Uco); // Ucode 출력 Table
		public JTable SymbolTable = new JTable(Symbol); // Label 출력 Table
		JScrollPane Codescroll = new JScrollPane(CodeTable); // CodeTable Scroll
		JScrollPane SymbolScroll = new JScrollPane(SymbolTable); // LabelTable Scroll
		
		/*출력을 위한 텍스트영역들*/
		JTextArea StackText = new JTextArea("-Stack-"); // 스택 출력 텍스트
		JTextArea MemoryText = new JTextArea("-Memory-"); // 메모리 출력 텍스트
		JTextArea ListText = new JTextArea("-List-"); // 리스트 출력 텍스트
		/*JScrollPane은 내용이 길어질때 스크롤을 만들어줌*/
		JScrollPane ListScroll = new JScrollPane(ListText); // ListText Scroll
		JScrollPane MemScroll = new JScrollPane(MemoryText);
		
		/*파일 입력을 위한 변수들*/
		String[] UcoName = {"factorial.uco","prime.uco","sigma.uco"};
		JComboBox LoadCom = new JComboBox(UcoName);
		String FileName;
		
		/*동작을 위한 버튼들*/
		JButton LoadBtn = new JButton("Load"); // uco파일을 받아올 버튼
		JButton StepBtn = new JButton("Step"); // 단계별로 uco파일 실행 시킬 버튼
		JButton RunBtn = new JButton("Run");	// 순차적으로 실행시킬 버튼
		JButton ListBtn = new JButton("Create lst"); // List 출력 버튼
		JButton FinishBtn = new JButton("확인"); // 확인 및 종료 버튼
	   
		JFrame JF = new JFrame();	 // 전체 틀
		JPanel JP1 = new JPanel();   // uco 메모리  스택  심볼테이블 결과값 JP3
		JPanel JP2 = new JPanel();   // List
		JPanel JP3 = new JPanel();   // JP4 JP5
		JPanel JP4 = new JPanel();   // Symbol
		JPanel JP5 = new JPanel();   // 콤보박스 버튼들
		
		
		
	public MainFrame(){
			cycle=0; // 총 명령어 접근 횟수 초기화
			MemoryApr=0; // 메모리 접근 횟수 초기화
			StackApr=0; // 스택 접근 횟수 초기화
			regPC=0;  // 메모리 pc 초기화
			regSP=-1; // 메모리 sp 초기화
			regBP=-1; // 메모리 bp 초기화
			maxLine=0; // 마지막 줄 초기화
			Memory = new int[1000]; //메모리 공간 1000개
			CommandCnt = new int[37]; //명령어 37개에 대한 명령어 접근 횟수
			label_info = new SymbolTable_[30]; //라벨 최대 30개
			asm_info = new CodeTable_[300];    //명령어 저장 공간 300개 
			
			JF.setTitle("U-code Interpreter"); // 제목
			JF.setDefaultCloseOperation(EXIT_ON_CLOSE);
			setLayout(null);
		   
			/*레이아웃 설정 및 컴포넌트 부착*/
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
		    
		    
		 
		    LoadBtn.addActionListener // Load 버튼 이벤트
		    (
		 			new ActionListener() 
	 				{
 						public void actionPerformed(ActionEvent e)
						{
 							/*파일을 새로받을 때 마다 초기화*/
 							LoadTrue = 1;
 							MemoryText.setText("-Memory-"); // Mem TextArea 초기화
 							StackText.setText("-Stack-");
							ListText.setText("-List-"); // List TextArea 초기화
							//ResultText.setText("-Result-"); // Result TextArea 초기화
							Uco.setNumRows(0);  // Ucode Table 커서 초기화
							Symbol.setNumRows(0); // Label Table 커서 초기화
							cycle=0; // 총 명령어 접근 횟수 초기화
							MemoryApr=0; // 메모리 접근 횟수 초기화
							StackApr=0; // 스택 접근 횟수 초기화
							regPC=0;  // 메모리 pc 초기화
							regSP=-1; // 메모리 sp 초기화
							regBP=-1; // 메모리 bp 초기화
							maxLine=0; // maxLine 초기화
							Memory = new int[1000]; //메모리 공간 1000개
							assemble.ResultString="";
							for(int i=0;i<70;i++)
							{
								Memory[i]=-9999;	
							}
							CommandCnt = new int[37]; //명령어 37개에 대한 명령어 접근 횟수
							label_info = new SymbolTable_[30];
							asm_info = new CodeTable_[300];
							
							// 파일 입력을 위한 절대경로
							FileName = "C:\\\\Users\\\\혁찬\\\\eclipse-workspace\\\\Test\\\\UCode 테스트\\\\"
										+LoadCom.getSelectedItem().toString();
							File file = new File(FileName);
							resultFile = file;
							
							BufferedReader br = null; // 문자를 읽어드리기 위한 버퍼리더
							int line = 0; // 줄 수를 출력하기 위한 변수
							int label_index = 0; // 라벨 배열의 인덱스

							try
							{
								FileReader fr = new FileReader(file); // 파일에 저장된 바이트를 유니코드 문자로 변환해서 읽어옴
								br = new BufferedReader(fr); //모두 String으로 읽어들임
								String s;
								String rest;
								
								while ((s = br.readLine()) != null)  //파일의 끝까지 모든 줄을 반환
								{
									int indexof; // %의 위치 인덱 스를 가지기 위한 변수
									indexof = s.indexOf("%"); // 한줄씩 반환한 문자열에서 %의 인덱스를 찾음
									
									if (indexof!=-1) // %라는 문자가 있으면
									{
										s = s.substring(0, indexof - 1); // S의  %문자 전까지의 문자열만 저장
									}
									String label = s.substring(0, 11);// 처음 0~11번째 전까지의 라벨 문자열을 넘겨줌 (0 ~ 10까지가 Label 자리로 약속)
									if (label.equals("           "))  //라벨이 없으면 넘어감
									{
										
									} 
									else //라벨이 있으면 라벨의 정보와 명령어 위치를 저장
									{
										label = label.replaceAll(" ", ""); // 공백 제거, " " -> "" 대체
										label_info[label_index] = new SymbolTable_(label, line); // label_info 객체를 통해 저장
										Symbol.addRow(new String[] {label,Integer.toString(line)}); //라벨값과, 그 인덱스 행 추가
										label_index++; //라벨을 저장하는 배열의 인덱스를 증가시킴
									}
									rest = s.substring(11); // 라벨을 뺀 문자열
									StringTokenizer str = new StringTokenizer(rest," "); // 공백 단위로 구분
									
									if(str.countTokens()==1)
									{
										String asm = str.nextToken();
										Uco.addRow(new String[] {Integer.toString(line), label,asm," "," "," "}); //명령어줄 행 추가
										asm_info[line] = new CodeTable_(label, asm," "," "," "); // asm_info 객체를 통해 저장
									}
									else if(str.countTokens()==2)
									{
										String asm = str.nextToken();
										String var1 = str.nextToken();
										Uco.addRow(new String[] {Integer.toString(line), label,asm,var1," "," "}); //명령어줄 행 추가
										asm_info[line] = new CodeTable_(label, asm,var1," "," "); // asm_info 객체를 통해 저장
									}
									else if(str.countTokens()==3)
									{
										String asm = str.nextToken();
										String var1 = str.nextToken();
										String var2 = str.nextToken();
										Uco.addRow(new String[] {Integer.toString(line), label,asm,var1,var2," "}); //명령어줄 행 추가
										asm_info[line] = new CodeTable_(label, asm,var1,var2," "); // asm_info 객체를 통해 저장
									}
									else if(str.countTokens()==4)
									{
										String asm = str.nextToken();
										String var1 = str.nextToken();
										String var2 = str.nextToken();
										String var3 = str.nextToken();
										Uco.addRow(new String[] {Integer.toString(line), label,asm,var1,var2,var3}); //명령어줄 행 추가
										asm_info[line] = new CodeTable_(label, asm,var1,var2,var3); // asm_info 객체를 통해 저장		 
									}

									line++;
								}
								br.close(); // 전부 반환 후 버퍼리더 닫기
								maxLine=line; // 마지막줄 정보 저장
								index_label = label_index; // 
								//ResultText.setText("-Result-");
							} catch (IOException e1) {
								
							}
							assemble.stack.clear(); // uco파일을 새로받고 동작하기 위해 스택초기화
						}
					}
			);
		   
		    StepBtn.addActionListener // Step 버튼 이벤트
			(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							if(LoadTrue == 1)
							{
								if(regPC==maxLine+1) // end 명령어가 실행됬을 때 다음 스텝버튼을 누르면 프로그램 종료 출력
								{
									return;
								}
								CodeTable.changeSelection(regPC,1,false, false); // CodeTable의 현재 PC에 포커스 주기
								
								assemble.runAssemble(asm_info[regPC]); // 명령어 실행, 인자는 해당명령어 정보 //asm_info 객체
								StackText.setText("-Stack-\n"); // 스택 TextArea 초기화
								MemoryText.setText("-Memory"); // 메모리 TextArea 초기화
								
								for (int i = 0; i < assemble.stack.size(); i++) //스택안에 쌓여있는 값을 출력
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
								MemoryText.setText("-Memory-\n"+"regBP : "+regBP+"\n"+"regSP : "+regSP+"\n"); //스택 bp와 sp 위치 출력
								for(int i=0; i < 1000; i++) // 메모리값 출력
								{
									MemoryText.setCaretPosition(0); // Step버튼 클릭시 스크롤을 내리기 위해
									MemoryText.append(i+"번지 :"+Memory[i]);
									MemoryText.append("\n");
								}
								regPC++;
							}
							else {}
						}
					}
			);
		    RunBtn.addActionListener // Run 버튼 이벤트
			(
					new ActionListener() 
					{
						public void actionPerformed(ActionEvent e)
						{
							if(LoadTrue == 1)
							{
								if(regPC==maxLine+1) // end 명령어가 실행됬을 때 다음 스텝버튼을 누르면 프로그램 종료 출력
								{
									return;
								}
								while(regPC<=maxLine)//regPC가 마지막 라인보다 작은 동안 
								{								
									
									assemble.runAssemble(asm_info[regPC]);	 //명령어 실행								
									regPC++;															
								}
								/*각 메모리 결과 출력*/
								MemoryText.setText("-Memory-\n"+"regBP : "+regBP+"\n"+"regSP : "+regSP+"\n"); //스택 bp와 sp 위치 출력
								for(int i=0; i < 1000; i++)  
								{
									MemoryText.setCaretPosition(0);
									MemoryText.append(i+"번지 :"+Memory[i]);
									MemoryText.append("\n");
								}
							}
							else {}
							
						}
					}
			);
		   
		    ListBtn.addActionListener // List 버튼 이벤트
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
									ListText.append(assemble.ResultString); // 수정필요
									ListText.append("\n###########################Statistics###########################\n");
									ListText.append("\n*************************Instruction  Count***********************************\n");
									for(int i=0; i<37; i++) // 명령어 실행 횟수 출력
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
									// lst 파일 만들기
									
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
		    FinishBtn.addActionListener  // 확인 버튼 클릭시 종료
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
		// TODO 자동 생성된 메소드 스텁
		MainFrame frame = new MainFrame();
	}

}


