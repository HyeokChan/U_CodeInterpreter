package level3;

import java.util.Stack;
import javax.swing.JOptionPane;


public class Assemble {
	
	int TVal=-1;
	int FVal=0;
	Stack <Integer> stack = new Stack <Integer>(); // CPU ������ ��Ÿ���� ���� ����
	int val, val2, val3;
	int pushCnt;	 
	MainFrame main; // ���������� ��ü ����
	public String ResultString = ""; 
	public Assemble(MainFrame main) { // �����ڸ� ���� ���������� �޾ƿ�
		this.main = main;  
	}
	
	int getAssemble(String asm)
	{
		int i=0;
		while(i<37)  // ��ɾ� �迭�� ��ü�ݺ����� ���� ����� ��ɾ��� �ε����� ã��
		{
			if(asm.equals(main.Command[i]))
			{
				return i;
			}
			i++;
		}
		return -1;
	}
	
	void bgn(int offset) // ���α׷��� ����, ���������� ������ ����
	{
		if(offset==0)
		{
			return;
		}
		main.regBP += 1; // �ʱⰪ -1�̴� bp���� ���� �ٴ��� ����Ű�� ��
		main.regSP += offset; // sp�� �������� ���� �ֻ���� ����Ű�� ��
	}
	
	void end() // ���α׷��� ��
	{
		main.regPC = main.maxLine; // ���α׷�ī������ ���� ���������� ����
		return; 
	}
	
	void proc(int offset) // �Լ��� ����, ���������� �� ������ ����
	{		
		main.regSP += offset;
	}
	
	void ret() // (return) �Լ��� �����ϰ� ����
	{
		main.regPC=main.Memory[main.regBP+1]; // pc�� ��������
		main.regSP=main.regBP-1; // sp���� bp-1������ ����
		main.regBP=main.Memory[main.regBP]; // bp���� ���� bp������ ����
	}
	
	void ldp() // �Լ��� �����ڵ��� ����
	{
		main.regSP+=2;
	}
	
	void push() // CPU ���ÿ� �÷��� �ִ� ������ ���� �޸� ���ÿ� ����
	{
		main.Memory[main.regSP+1+pushCnt]=stack.pop();
		pushCnt++;
	}
	
	void call(CodeTable_ asm)
	{
		int i = 0;
		String calledLabel = asm.val1.replaceAll(" ", ""); // �̵��� ���� ��������

		if(calledLabel.equals("read")) // ����ڷκ��� �о����
		{
			String valstr = JOptionPane.showInputDialog(null, "Input", "�������� �Է°���", JOptionPane.OK_CANCEL_OPTION);
			int num;
			if(valstr.isEmpty())
			{}
			else { // lda ��ɾ�� ���ÿ� �ö�� ���� ���� �޸��� �ּҷ� ����ؼ� �Է°��� ����
				num =  Integer.parseInt(valstr);
				main.Memory[main.Memory[main.regSP+1]] = num;
			}
			main.regSP -=2;
			pushCnt=0;
			return;
		}
		else if(calledLabel.equals("write")) // ���� top�� ���
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
		else if(calledLabel.equals("lf")) // �ٹٲ�
		{
			return;
		}
		while(i<main.index_label) // ���� ��ġ�� ã�� regPC���� ����
		{
			if(asm.val1.equals(main.label_info[i].label))
			{
				main.Memory[main.regSP-1] = main.regBP; // ���� bp�� ����
				main.Memory[main.regSP] = main.regPC; // ���� pc�� ����
				main.regBP = main.regSP-1; // bp�� sp-1
				main.regPC = main.label_info[i].line-1; 
				// regPC�� label�� �ִ� ��ɾ� ��ĭ ���� ���� ����Ŵ ,step�� ���ؼ� pc���� 1�����Ǽ� ���������δ� ���� ��ɾ� ���ට ���� ���� ó���ϰԵ�
				pushCnt=0;
				break;
			}
			i++;
		}
	}
	
	void lod(int block, int offset) // (load) b ��� n �������� �����͸� ���ÿ� ���� 
	{
		if(block==1) // �������� ��� 1
		{
			stack.push(main.Memory[offset]); // �������� ��ġ�� �ִ� ���� ���ÿ� ����
		}
		else if(block ==2) // �Լ����� ���Ǵ� �� bp�� offset���� ���Ǵ� �ּ�
		{
			stack.push(main.Memory[main.regBP+offset+2]); // bp���ؿ����� offset������ ���Ǵ� ��ġ�� ���� ���ÿ� ����
			// +2�� ���� bp, pc �� ���嶧��
		}
	}
	
	void lda(int block, int offset) // (load address) ��ϰ� ���������� ���Ǵ� ���� �޸� ������ ���ÿ� ����
	{
		if(block==1) // �������� ��� 1
		{
			stack.push(offset); //����� 1�̸� ���������ϱ� offset�� �־���
		}
		else if(block ==2) //�Լ����� ���Ǵ� �� bp�� offset���� ���Ǵ� �ּ�
		{
			stack.push(main.regBP+offset+2);
			//+2�� ���� bp, pc �� ���嶧��
		}
	}
	
	void str(int block, int offset) // (store) ���� ������� ���� b�� n���� ���Ǵ� �ּ��� �޸𸮿� ����
	{
		if(block==1) // �������� ��� 1
		{
			main.Memory[offset]=stack.pop(); // ����������� ������ top���� �޸��� offset��ġ�� ���� ����
		}
		else if(block ==2) // �Լ����� ���Ǵ� �� bp�� offset���� ���Ǵ� �ּ�
		{
			main.Memory[main.regBP+offset+2] = stack.pop(); // block�� ���� regBP+offset���� ���Ǵ� �޸��� ��ġ�� ������ ���� ����
			// +2 �� �Ѻ���� ����ϱ� ������ ����Bp�� Pc�� �������� ���� �ö�
		}
	}
	
	void ldi() // (load indirect) ���� �ּҹ��� �̿��� �޸��� ���� ���ÿ� ������
	{
		int index=stack.pop();
		stack.push(main.Memory[index]);
	}
	
	void sti() // (store indirect) ���ÿ��� ó�� pop�� ���� ���� pop�� ���� �ּ�
	{
		int temp = stack.pop();
		main.Memory[stack.pop()] = temp;  // 1.����0�� �޸��ּ� 4������ 
	}
	
	void not() // �ǿ������� �������� ����
	{
		int val = stack.pop();
		if(val == TVal)
			stack.push(FVal);
		else
			stack.push(TVal);
	}
	
	void neg() // (negation) �ǿ������� ���簪�� ����
	{
		stack.push(-1 * stack.pop());
	}
	
	void inc() // (increment) �ǿ������� ���� �ϳ� ����
	{
		stack.push(stack.pop() + 1);
	}

	void dec() // (decrement) �ǿ������� ���� �ϳ� ����
	{
		stack.push(stack.pop() - 1);
	}

	void dup() // (duplicate) ������ ������� ���� �����ؼ� ���ÿ� ����
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
	
	
	void ujp(CodeTable_ asm) // (unconditional jump) ������ label�� ������ �̵�
	{
		int i = 0;
		while(i<main.index_label) // ���� ��ġ�� ã�� regPC���� ����
		{
			if(asm.val1.equals(main.label_info[i].label))
			{
				main.regPC = main.label_info[i].line-1; 
				// regPC�� label�� �ִ� ��ɾ� ��ĭ ���� ���� ����Ŵ step�� ���ؼ� pc���� 1�����Ǽ� ���������δ� ���� ��ɾ� ���ට ���� ���� ó���ϰԵ�
				break;
			}

			i++;
		}
	}
	
	
	void fjp(CodeTable_ asm) // (jump on false)  stack[top]�� ���� �����̸� label�� �̵�
	{
		int i=0;
		while(i<main.index_label) // ���� ��ġ�� ã�� regPC���� ����
		{
			if(asm.val1.equals(main.label_info[i].label))
			{
				main.regPC = main.label_info[i].line-1; 
				// regPC�� label�� �ִ� ��ɾ� ��ĭ ���� ���� ����Ŵ step�� ���ؼ� pc���� 1�����Ǽ� ���������δ� ���� ��ɾ� ���ට ���� ���� ó���ϰԵ�
				break;
			}
			i++;
		}
	}
	
	
	void runAssemble(CodeTable_ asm) // ��ɾ��� opcode�� �Ѱ������
	{
		int index=-1;
		index = getAssemble(asm.asm);
		main.cycle++;
		switch(asm.asm) // ��ɾ� �Ѱ���, ��ġ�ϴ� ��ɾ ����
		{
			case "nop":  // �ƹ��۾��� �������� ������ �ַ� ���̺� �� ���α׷�
			main.CommandCnt[index]++;
			break;
			
			case "bgn":  // ���α׷��� ������
				bgn(Integer.parseInt(asm.val1));
				main.CommandCnt[index]++; // ��ɾ� ����Ƚ�� ����
				break;
				
			case "sym":  // ������ ���� �� ũ�� ����
				main.CommandCnt[index]++;
				break;
				
			case "end":
				end();
				main.CommandCnt[index]++;
				main.StackText.setText("-Stack-\n���α׷� ����.");
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
				
			case "ldc": // ���ÿ� val1 �� �ֱ�
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
