package test.persistence;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.Student;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import exception.ClienteException;
import persistence.AlunoDAO;
import persistence.FactoryConnection;

public class AlunoDAOTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	
	@Test
	public void testInstance() {
		assertTrue("Instanciando AlunoDAO", AlunoDAO.getInstance() instanceof AlunoDAO);
	}
	
	@Test
	public void testSingleton() {
		AlunoDAO student1 = AlunoDAO.getInstance();
		AlunoDAO student2 = AlunoDAO.getInstance();
		assertSame("Testando o Padrao Singleton", student1, student2);
	}
	

	@Test
	public void testInsert() throws ClienteException, SQLException {
		boolean resultado = false;
		Student student = new Student("Incluindo", "040.757.021-70", "098767", "9999-9999", "aluno@email");
		AlunoDAO.getInstance().insert(student);
		
		resultado = select(student);
		
		if(resultado){
			delete(student);
		}
		assertTrue("Insert a student into the database", resultado);
	}
	
	@Test (expected= ClienteException.class)
	public void testIncluirNulo() throws ClienteException, SQLException {
		AlunoDAO.getInstance().insert(null);
	}
	
	@Test (expected= ClienteException.class)
	public void testIncluirComMesmoCpf() throws ClienteException, SQLException {
		boolean resultado = true;
		Student student = new Student("Incluindo", "040.757.021-70", "098765", "1111-1111", "aluno@email");
		Student student2 = new Student("Incluindo CPF Igual", "040.747.021-70", "987654", "2222-2222", "aluno2@email");
		AlunoDAO.getInstance().insert(student);
		try{
			AlunoDAO.getInstance().insert(student2);
			delete(student2);
			
		} finally {
			delete(student);
			
			resultado = select(student2);
		}
		
		assertFalse("Include a student with the same CPF", resultado);
	}
	@Test (expected= ClienteException.class)
	public void testIncluirComMesmaMatricula() throws ClienteException, SQLException {
		boolean resultado = true;
		Student student = new Student("Incluindo", "040.757.021-70", "111111", "1111-1111", "aluno@email");
		Student student2 = new Student("Incluindo Matricula Igual", "490.491.781-20", "111111", "2222-2222", "aluno2@email");
		AlunoDAO.getInstance().insert(student);
		try{
			AlunoDAO.getInstance().insert(student2);
			delete(student2);
			
		} finally {
			delete(student);
			
			resultado = select(student2);
		}
		
		assertFalse("Include a student with the same Enrollment Number", resultado);
	}
	@Test (expected= ClienteException.class)
	public void testIncluirJaExistente() throws ClienteException, SQLException {
		boolean resultado = true;
		Student aluno = new Student("Incluindo", "040.757.021-70", "58801", "3333-3333", "aluno@email");
		Student aluno2 = new Student("Incluindo", "040.757.021-70", "58801", "3333-3333", "aluno@email");
		AlunoDAO.getInstance().insert(aluno);
		try{
			AlunoDAO.getInstance().insert(aluno2);
			delete(aluno2);
			
		} finally {
			delete(aluno);
			resultado = select(aluno2);
		}
		
		assertFalse("Teste de Inclus�o.", resultado);
	}
	
	
	
	@Test
	public void testAlterar() throws ClienteException, SQLException {
		boolean resultado = false;
		Student a = new Student("Incluindo", "044.437.451-57", "123456", "1234-5678", "Nome@email");
		Student an = new Student("Alterando", "232.738.601-20", "098765", "(123)4567-8899", "email@Nome");
		insert(a);
		
		AlunoDAO.getInstance().modify(a, an);
		
		resultado = select(an);
		boolean resultado2 =  select(a);
		if(resultado)
			delete(a);
		if(resultado2)
			delete(a);
		
		assertTrue("Teste de Altera��o.", resultado == true && resultado2 == false);
	}
	
	@Test (expected= ClienteException.class)
	public void testAlterarPrimeiroArgNulo() throws ClienteException, SQLException {
		Student an = new Student("Alterando", "00.757.021-70", "123456", "(999)9999-9999", "aluno@email");
		AlunoDAO.getInstance().modify(null, an);
	}
	
	@Test (expected= ClienteException.class)
	public void testAlterarSegundoArgNulo() throws ClienteException, SQLException {
		Student an = new Student("Alterando", "00.757.021-70", "123456", "(999)9999-9999", "aluno@email");
		AlunoDAO.getInstance().modify(an, null);
	}
	@Test (expected= ClienteException.class)
	public void testAlterarNaoExistente() throws ClienteException, SQLException {
		boolean resultado = true;
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "1111-1111", "aluno@email");
		Student an = new Student("Alterando", "490.491.781-20", "098765", "(999)9999-9999", "email@aluno");
		
		try{
			AlunoDAO.getInstance().modify(a, an);
		} finally {
			resultado = select(an);
			if(resultado)
				delete(an);
		}
		assertFalse("Teste de Altera��o.", resultado);
	}
	@Test (expected= ClienteException.class)
	public void testAlterarParaJaExistente() throws ClienteException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		Student a = new Student("Incluindo", "040.757.021-70", "058801", "9999-9999", "aluno@email");
		Student an = new Student("Incluindo", "040.757.021-70", "058801", "9999-9999", "aluno@email");
		
		insert(a);
		
		try{
			AlunoDAO.getInstance().modify(a, an);
		} finally {
			resultado = select(an);
			resultado2 =  select(a);
			if(resultado)
				delete(an);
			if(resultado2)
				delete(a);
		}
		assertTrue("Teste de Altera��o.", resultado == false && resultado2 == true);
	}
	@Test (expected= ClienteException.class)
	public void testAlterarParaCpfExistente() throws ClienteException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		boolean resultado3 = false;
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		Student an = new Student("Incluindo Segundo", "490.491.781-20", "1234", "4444-4444", "novoAluno@email");
		Student ann = new Student("Incluindo Segundo", "040.757.021-70", "1234", "4444-4444", "novoAluno@email");
		
		insert(a);
		insert(an);
		
		try{
			AlunoDAO.getInstance().modify(an, ann);
		} finally {
			resultado = select(an);
			
			resultado2 = select(a);
			
			resultado3 = select(ann);
			
			if(resultado)
				delete(an);
			if(resultado2)
				delete(a);
			if(resultado3)
				delete(ann);
		}
		assertTrue("Teste de Altera��o.", resultado == true && resultado2 == true && resultado3 == false);
	}
	@Test (expected= ClienteException.class)
	public void testAlterarParaMatriculaExistente() throws ClienteException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		boolean resultado3 = false;
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-99999", "aluno@email");
		Student an = new Student("Incluindo Segundo", "490.491.781-20", "0987", "5555-5555", "alunoNovo@email");
		Student ann = new Student("Incluindo Segundo", "490.491.781-20", "123456", "5555-5555", "alunoNovo@email");
		
		insert(a);
		insert(an);
		
		try{
			AlunoDAO.getInstance().modify(an, ann);
		} finally {
			resultado = select(an);
			
			resultado2 = select(a);
			
			resultado3 = select(ann);
			
			if(resultado)
				delete(an);
			if(resultado2)
				delete(a);
			if(resultado3)
				delete(ann);
		}
		assertTrue("Teste de Altera��o.", resultado == true && resultado2 == true && resultado3 == false);
	}
	@Ignore // (expected= ClienteException.class)
	public void testAlterarEnvolvidoEmReserva() throws ClienteException, SQLException {
		fail();
	}
	
	
	
	@Test
	public void testExcluir() throws ClienteException, SQLException {
		boolean resultado = true;
		Student a = new Student("Incluindo", "040.757.021-70", "058801", "9999-9999", "aluno@email");
		
		insert(a);
		
		AlunoDAO.getInstance().delete(a);
		

		resultado =  select(a);
		
		if(resultado)
			delete(a);
		
		assertFalse("Teste de Altera��o.", resultado);
	}
	@Test (expected= ClienteException.class)
	public void testExcluirNulo() throws ClienteException, SQLException {
		AlunoDAO.getInstance().delete(null);
	}
	@Ignore // (expected= ClienteException.class)
	public void testExcluirEnvolvidoEmReserva() throws ClienteException, SQLException {
		fail();
	}
	@Test (expected= ClienteException.class)
	public void testExcluirNaoExistente() throws ClienteException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		AlunoDAO.getInstance().delete(a);
	}
	
	
	
	@Test
	public void testBuscarNome() throws ClienteException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		
		insert(a);
		
		Vector<Student> vet = AlunoDAO.getInstance().searchByName("Incluindo");

		delete(a);
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarCpf() throws ClienteException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		
		insert(a);
		
		Vector<Student> vet = AlunoDAO.getInstance().searchByCpf("040.757.021-70");

		delete(a);
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarMatricula() throws ClienteException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		
		insert(a);
		
		Vector<Student> vet = AlunoDAO.getInstance().searchByEnrollmentNumber("123456");

		delete(a);
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarTelefone() throws ClienteException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		
		insert(a);
		
		Vector<Student> vet = AlunoDAO.getInstance().searchByPhoneNumber("9999-9999");

		delete(a);
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarEmail() throws ClienteException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		
		insert(a);
		
		Vector<Student> vet = AlunoDAO.getInstance().searchByEmail("aluno@email");

		delete(a);
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	
	
	

	private void insert(Student aluno) throws SQLException {

		this.executaNoBanco("INSERT INTO "
				+ "aluno (nome, cpf, telefone, email, matricula) VALUES ("
				+ "\"" + aluno.getName() + "\", " + "\"" + aluno.getCpf()
				+ "\", " + "\"" + aluno.getPhoneNumber() + "\", " + "\""
				+ aluno.getEmail() + "\", " + "\""
				+ aluno.getEnrollmentNumber() + "\");");
	}

	private void delete(Student aluno) throws SQLException {

		this.executaNoBanco("DELETE FROM aluno WHERE " + "aluno.nome = \""
				+ aluno.getName() + "\" and " + "aluno.cpf = \""
				+ aluno.getCpf() + "\" and " + "aluno.telefone = \""
				+ aluno.getPhoneNumber() + "\" and " + "aluno.email = \""
				+ aluno.getEmail() + "\" and " + "aluno.matricula = \""
				+ aluno.getEnrollmentNumber() + "\";");
	}

	private boolean select(Student aluno) throws SQLException {

		boolean isOnDatabase;

		isOnDatabase = this.estaNoBanco("SELECT * FROM aluno WHERE "
				+ "aluno.nome = \"" + aluno.getName() + "\" and "
				+ "aluno.cpf = \"" + aluno.getCpf() + "\" and "
				+ "aluno.telefone = \"" + aluno.getPhoneNumber() + "\" and "
				+ "aluno.email = \"" + aluno.getEmail() + "\" and "
				+ "aluno.matricula = \"" + aluno.getEnrollmentNumber() + "\";");

		return isOnDatabase;
	}

	private void executaNoBanco(String msg) throws SQLException {

		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement(msg);
		pst.executeUpdate();
		pst.close();
		con.close();
	}

	private boolean estaNoBanco(String query) throws SQLException {

		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement(query);
		ResultSet rs = pst.executeQuery();

		if (!rs.next()) {
			rs.close();
			pst.close();
			con.close();
			return false;
		} else {
			rs.close();
			pst.close();
			con.close();
			return true;
		}
	}
}


	