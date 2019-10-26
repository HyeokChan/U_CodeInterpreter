package level3;
//SymbolTable 저장
public class SymbolTable_ {
	// 라벨 정보 저장을 위한 클래스
	String label;
	int line;

	public SymbolTable_(String label, int line) 
	{
		this.label = label;
		this.line = line;
	}
}