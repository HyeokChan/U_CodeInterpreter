package level3;

import java.util.Stack;
import javax.swing.JOptionPane;


public class Assemble {
	
	int TVal=-1;
	int FVal=0;
	Stack <Integer> stack = new Stack <Integer>(); // CPU 스택을 나타내기 위한 변수
	int val, val2, val3;
	int pushCnt;	 
	MainFrame main; // 메인프레임 객체 생성
	public String ResultString = ""; 
	public Assemble(MainFrame main) { // 생성자를 통해 메인프레임 받아옴
		this.main = main;  
	}
	
	int getAssemble(String asm)
	{
		int i=0;
		while(i<37)  // 명령어 배열을 전체반복문을 돌려 실행된 명령어의 인덱스를 찾음
		{
			if(asm.equals(main.Command[i]))
			{
				return i;
			}
			i++;
		}
		return -1;
	}
	
	void bgn(int offset) // 프로그램의 시작, 전역변수의 개수를 선언
	{
		if(offset==0)
		{
			return;
		}
		main.regBP += 1; // 초기값 -1이던 bp값을 가장 바닥을 가리키게 함
		main.regSP += offset; // sp는 전역변수 범위 최상단을 가리키게 함
	}
	
	void end() // 프로그램의 끝
	{
		main.regPC = main.maxLine; // 프로그램카운터의 값을 마지막으로 설정
		return; 
	}
	
	void proc(int offset) // 함수의 시작, 지역변수의 총 개수를 선언
	{		
		main.regSP += offset;
	}
	
	void ret() // (return) 함수를 종료하고 복귀
	{
		main.regPC=main.Memory[main.regBP+1]; // pc값 변경해줌
		main.regSP=main.regBP-1; // sp값은 bp-1값으로 수정
		main.regBP=main.Memory[main.regBP]; // bp값은 이전 bp값으로 변경
	}
	
	void ldp() // 함수의 실인자들을 저장
	{
		main.regSP+=2;
	}
	
	void push() // CPU 스택에 올려져 있는 실인자 값을 메모리 스택에 저장
	{
		main.Memory[main.regSP+1+pushCnt]=stack.pop();
		pushCnt++;
	}
	
	void call(CodeTable_ asm)
	{
		int i = 0;
		String calledLabel = asm.val1.replaceAll(" ", ""); // 이동할 라벨의 공백제거

		if(calledLabel.equals("read")) // 사용자로부터 읽어오기
		{
			String valstr = JOptionPane.showInputDialog(null, "Input", "정수값만 입력가능", JOptionPane.OK_CANCEL_OPTION);
			int num;
			if(valstr.isEmpty())
			{}
			else { // lda 명령어로 스택에 올라온 값을 스택 메모리의 주소로 사용해서 입력값을 저장
				num =  Integer.parseInt(valstr);
				main.Memory[main.Memory[main.regSP+1]] = num;
			}
			main.regSP -=2;
			pushCnt=0;
			return;
		}
		else if(calledLabel.equals("write")) // 스택 top값 출력
		{
			if(main.Memory[main.regSP+1]>=1000000000 || main.Memory[main.regSP+1]<=-1000000000)
			{ 
				ResultString = ResultString.concat("overflow\n");
				
			}
			else 
			{
				ResultString = ResultString.concat(Integer.toString(main.Memory[main.regSP+1])+"\n");
			}
			
			main.regSP -=2;
			pushCnt=0;
			return;
		}
		else if(calledLabel.equals("lf")) // 줄바꿈
		{
			return;
		}
		while(i<main.index_label) // 라벨의 위치를 찾아 regPC값을 변경
		{
			if(asm.val1.equals(main.label_info[i].label))
			{
				main.Memory[main.regSP-1] = main.regBP; // 현재 bp값 저장
				main.Memory[main.regSP] = main.regPC; // 현재 pc값 저장
				main.regBP = main.regSP-1; // bp는 sp-1
				main.regPC = main.label_info[i].line-1; 
				// regPC가 label이 있는 명령어 한칸 이전 줄을 가르킴 ,step을 통해서 pc값이 1증가되서 실질적으로는 다음 명령어 실행때 라벨의 줄을 처리하게됨
				pushCnt=0;
				break;
			}
			i++;
		}
	}
	
	void lod(int block, int offset) // (load) b 블록 n 오프셋의 데이터를 스택에 넣음 
	{
		if(block==1) // 전역변수 블록 1
		{
			stack.push(main.Memory[offset]); // 전역변수 위치에 있는 값을 스택에 저장
		}
		else if(block ==2) // 함수에서 사용되는 블럭 bp와 offset으로 계산되는 주소
		{
			stack.push(main.Memory[main.regBP+offset+2]); // bp기준에서의 offset값으로 계산되는 위치의 값을 스택에 넣음
			// +2는 이전 bp, pc 값 저장때문
		}
	}
	
	void lda(int block, int offset) // (load address) 블록과 오프셋으로 계산되는 실제 메모리 번지를 스택에 넣음
	{
		if(block==1) // 전역변수 블록 1
		{
			stack.push(offset); //블록이 1이면 전역변수니깐 offset을 넣어줌
		}
		else if(block ==2) //함수에서 사용되는 블럭 bp와 offset으로 계산되는 주소
		{
			stack.push(main.regBP+offset+2);
			//+2는 이전 bp, pc 값 저장때문
		}
	}
	
	void str(int block, int offset) // (store) 스택 꼭대기의 값을 b와 n으로 계산되는 주소의 메모리에 저장
	{
		if(block==1) // 전역변수 블록 1
		{
			main.Memory[offset]=stack.pop(); // 전역변수라면 스택의 top값을 메모리의 offset위치에 값을 저장
		}
		else if(block ==2) // 함수에서 사용되는 블럭 bp와 offset으로 계산되는 주소
		{
			main.Memory[main.regBP+offset+2] = stack.pop(); // block과 현재 regBP+offset으로 계산되는 메모리의 위치에 스택의 값을 넣음
			// +2 는 한블록을 사용하기 때문에 이전Bp와 Pc를 저장으로 인해 올라감
		}
	}
	
	void ldi() // (load indirect) 간접 주소법을 이용해 메모리의 값을 스택에 가져옴
	{
		int index=stack.pop();
		stack.push(main.Memory[index]);
	}
	
	void sti() // (store indirect) 스택에서 처음 pop은 변수 다음 pop은 절대 주소
	{
		int temp = stack.pop();
		main.Memory[stack.pop()] = temp;  // 1.숫자0을 메모리주소 4번지에 
	}
	
	void not() // 피연산자의 진리값을 변경
	{
		int val = stack.pop();
		if(val == TVal)
			stack.push(FVal);
		else
			stack.push(TVal);
	}
	
	void neg() // (negation) 피연산자의 음양값을 변경
	{
		stack.push(-1 * stack.pop());
	}
	
	void inc() // (increment) 피연산자의 값이 하나 증가
	{
		stack.push(stack.pop() + 1);
	}

	void dec() // (decrement) 피연산자의 값이 하나 감소
	{
		stack.push(stack.pop() - 1);
	}

	void dup() // (duplicate) 스택의 꼭대기의 값을 복사해서 스택에 넣음
	{
		stack.push(stack.peek());
	}

	void add()
	{
		stack.push(stack.pop() + stack.pop());
	}

	void sub()
	{
		int top=stack.pop();
		stack.push(stack.pop() - top);
	}

	void mult()
	{
		stack.push(stack.pop() * stack.pop());
	}

	void div()
	{
		int val2 = stack.pop();
		int val3 = stack.pop();
		stack.push(val3 / val2);
	}

	void mod()
	{
		int val2 = stack.pop();
		int val3 = stack.pop();
		stack.push(val3 % val2);
	}

	void gt()
	{
		val2 = stack.pop();
		val3 = stack.pop();
		if(val2 < val3)
			stack.push(TVal);
		else
			stack.push(FVal);
	}

	void lt()
	{
		val2 = stack.pop(); 
		val3 = stack.pop();
		if(val2 > val3)
			stack.push(TVal);
		else
			stack.push(FVal);
	}

	void ge()
	{
		val2 = stack.pop();
		val3 = stack.pop();
		if(val2 <= val3)
			stack.push(TVal);
		else
			stack.push(FVal);
	}

	void le() 
	{
		val2 = stack.pop();
		val3 = stack.pop();
		if(val2 >= val3)
			stack.push(TVal);
		else
			stack.push(FVal);
	}

	void eq()
	{
		val2 = stack.pop();
		val3 = stack.pop();
		if(val2 == val3)
			stack.push(TVal);
		else
			stack.push(FVal);
	}

	void ne() // (not equal)
	{
		val2 = stack.pop();
		val3 = stack.pop();
		if(val2 != val3)
			stack.push(TVal);
		else
			stack.push(FVal);
	}

	void and()
	{
		val = stack.pop() & stack.pop();
		if(val == 1)
			stack.push(TVal);
		else
			stack.push(FVal);
	}

	void or()
	{
		val = stack.pop() | stack.pop();
		if(val == 1)
			stack.push(TVal);
		else
			stack.push(FVal);
	}

	void swp() // (swap)
	{
		val2 = stack.pop();
		val3 = stack.pop();
		stack.push(val2);
		stack.push(val3);
	}
	
	
	void ujp(CodeTable_ asm) // (unconditional jump) 지정한 label로 무조건 이동
	{
		int i = 0;
		while(i<main.index_label) // 라벨의 위치를 찾아 regPC값을 변경
		{
			if(asm.val1.equals(main.label_info[i].label))
			{
				main.regPC = main.label_info[i].line-1; 
				// regPC가 label이 있는 명령어 한칸 이전 줄을 가르킴 step을 통해서 pc값이 1증가되서 실질적으로는 다음 명령어 실행때 라벨의 줄을 처리하게됨
				break;
			}

			i++;
		}
	}
	
	
	void fjp(CodeTable_ asm) // (jump on false)  stack[top]의 값이 거짓이면 label로 이동
	{
		int i=0;
		while(i<main.index_label) // 라벨의 위치를 찾아 regPC값을 변경
		{
			if(asm.val1.equals(main.label_info[i].label))
			{
				main.regPC = main.label_info[i].line-1; 
				// regPC가 label이 있는 명령어 한칸 이전 줄을 가르킴 step을 통해서 pc값이 1증가되서 실질적으로는 다음 명령어 실행때 라벨의 줄을 처리하게됨
				break;
			}
			i++;
		}
	}
	
	
	void runAssemble(CodeTable_ asm) // 명령어의 opcode를 넘겨줘야함
	{
		int index=-1;
		index = getAssemble(asm.asm);
		main.cycle++;
		switch(asm.asm) // 명령어 넘겨줌, 일치하는 명령어에 멈춤
		{
			case "nop":  // 아무작업도 수행하지 않으며 주로 레이블 등 프로그램
			main.CommandCnt[index]++;
			break;
			
			case "bgn":  // 프로그램의 시작점
				bgn(Integer.parseInt(asm.val1));
				main.CommandCnt[index]++; // 명령어 실행횟수 증가
				break;
				
			case "sym":  // 변수의 저장 및 크기 설정
				main.CommandCnt[index]++;
				break;
				
			case "end":
				end();
				main.CommandCnt[index]++;
				main.StackText.setText("-Stack-\n프로그램 종료.");
				return;
			
			case "proc":
				proc(Integer.parseInt(asm.val1));
				main.CommandCnt[index]++;
				break;

			case "ret":
				ret();
				main.CommandCnt[index]++;
				break;

			case "ldp":
				ldp();
				main.CommandCnt[index]++;
				break;
				
			case "push":
				push();
				main.CommandCnt[index]++;
				main.MemoryApr++;
				main.StackApr++;
				break;
				
			case "call":
				call(asm);
				main.CommandCnt[index]++;
				break;		
				
			case "lod":
				lod(Integer.parseInt(asm.val1), Integer.parseInt(asm.val2));
				main.CommandCnt[index]++;
				main.MemoryApr++;
				main.StackApr++;
				break;
				
			case "lda":
				lda(Integer.parseInt(asm.val1), Integer.parseInt(asm.val2));
				main.CommandCnt[index]++;
				main.MemoryApr++;
				main.StackApr++;
				break;
				
			case "ldc": // 스택에 val1 값 넣기
				stack.push(Integer.parseInt(asm.val1));
				main.CommandCnt[index]++;
				main.MemoryApr++;
				main.StackApr++;
				break;
				
			case "str":
				try
				{
					str(Integer.parseInt(asm.val1), Integer.parseInt(asm.val2));
				}
				catch( Exception e ){}
				
				main.CommandCnt[index]++;
				main.MemoryApr++;
				main.StackApr++;
				break;
				
			case "ldi":
				ldi();
				main.CommandCnt[index]++;
				main.MemoryApr++;
				main.StackApr++;
				break;
				
			case "sti":
				sti();
				main.CommandCnt[index]++;
				main.MemoryApr++;
				main.StackApr++;
				break;
				
			case "not":
				not();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "neg":
				neg();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "inc":
				inc();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "dec":
				dec();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "dup":
				dup();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "add":
				add();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "sub":
				sub();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "mult":
				mult();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "div":
				div();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "mod":
				mod();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "gt":
				gt();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "lt":
				lt();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "ge":
				ge();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "le":
				le();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "eq":
				eq();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "ne":
				ne();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "and":
				and();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "or":
				or();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;
				
			case "swp":
				swp();
				main.CommandCnt[index]++;
				main.StackApr++;
				break;

			case "ujp":
				ujp(asm);
				main.CommandCnt[index]++;
				break;
				
			case "tjp":
				if(stack.pop() == TVal)
				{
					ujp(asm);
				}
				main.CommandCnt[index]++;
				break;
				
			case "fjp":
				if(stack.pop() == FVal)
				{
					fjp(asm);
				}
				main.CommandCnt[index]++;
				break;
		}
	}
}
