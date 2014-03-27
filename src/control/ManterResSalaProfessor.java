/**
  ManterResSalaProfessor Manages the reservations made by teacher.
  https://github.com/ParleyMartins/Tecnicas/blob/estiloDesign/src/control/
  /ManterResSalaProfessor.java
 */
package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.ResSalaProfessorDAO;

import model.Professor;
import model.ReservaSalaProfessor;
import model.Sala;
import exception.ClienteException;
import exception.PatrimonioException;
import exception.ReservaException;

public class ManterResSalaProfessor {

	private static ManterResSalaProfessor instance;
	
	private Vector <ReservaSalaProfessor> rev_sala_professor_vet = new Vector <ReservaSalaProfessor>( );

	private ManterResSalaProfessor ( ) {

		// Blank constructor.
	}

	// Singleton implementation.
	public static ManterResSalaProfessor getInstance ( ) {

		if (instance == null)
			instance = new ManterResSalaProfessor( );
		return instance;
	}

	// Returns the room reservation made ​​​​by students in a month period.
	public Vector <ReservaSalaProfessor> buscarPorData (String data)
			throws SQLException, ClienteException, PatrimonioException,
			ReservaException {

		return ResSalaProfessorDAO.getInstance( ).buscarPorData(data);
	}

	// Returns all the reservations made ​​by teacher
	public Vector <ReservaSalaProfessor> getResProfessorSala_vet ( )
			throws SQLException, ClienteException, PatrimonioException,
			ReservaException {

		this.rev_sala_professor_vet = ResSalaProfessorDAO.getInstance( )
				.buscarTodos( );
		return this.rev_sala_professor_vet;
	}

	// Include new reservation in the database.
	public void inserir (Sala sala, Professor prof,
			String data, String hora, String finalidade)
			throws SQLException, ReservaException {

		ReservaSalaProfessor reserva = new ReservaSalaProfessor(data, hora,
				sala, finalidade, prof);
		ResSalaProfessorDAO.getInstance( ).incluir(reserva);
		this.rev_sala_professor_vet.add(reserva);
	}

	// Update reservation info from the database.
	public void alterar (String finalidade, ReservaSalaProfessor reserva)
			throws SQLException, ReservaException {

		ReservaSalaProfessor reserva_old = new ReservaSalaProfessor(
				reserva.getData( ), reserva.getHora( ), reserva.getSala( ),
				reserva.getFinalidade( ), reserva.getProfessor( ));

		reserva.setFinalidade(finalidade);
		ResSalaProfessorDAO.getInstance( ).alterar(reserva_old, reserva);

	}

	// Remove the reservation made by a teacher.
	public void excluir (ReservaSalaProfessor reserva) throws SQLException,
			ReservaException {

		ResSalaProfessorDAO.getInstance( ).excluir(reserva);
		this.rev_sala_professor_vet.remove(reserva);
	}
}