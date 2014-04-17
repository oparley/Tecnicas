/**
ManterSala
Include the code and description of the room, change, and delete devices.
https://github.com/ParleyMartins/Tecnicas/tree/master/src/control/ManterSala.java
*/
package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.SalaDAO;
import exception.PatrimonioException;
import model.Sala;

public class ManterSala {

	private static ManterSala instance;

	private Vector <Sala> roomsVec = new Vector <Sala>();

	private ManterSala ( ) {

		// Blank constructor.
	}

	// This constructor provides the singleton implementation.
	public static ManterSala getInstance ( ) {

		if (instance == null)
			instance = new ManterSala();
		return instance;
	}

	// Gets a vector of room.
	public Vector <Sala> getSalas_vet ( ) throws SQLException,
			PatrimonioException {

		this.roomsVec = SalaDAO.getInstance().buscarTodos();
		return this.roomsVec;
	}

	// This method include code and description of the room in the database.
	public void inserir (String roomCode, String roomDescription,
			String capacity)
			throws PatrimonioException, SQLException {

		Sala sala = new Sala(roomCode, roomDescription, capacity);
		SalaDAO.getInstance().incluir(sala);
		this.roomsVec.add(sala);
	}

	// This method Update code and description info in the database.
	public void alterar (String roomCode, String roomDescription,
			String capacity,
			Sala newRoom) throws PatrimonioException, SQLException {

		Sala oldRoom = new Sala(newRoom.getCodigo(), newRoom.getDescricao(),
				newRoom.getCapacidade());
		newRoom.setCodigo(roomCode);
		newRoom.setDescricao(roomDescription);
		newRoom.setCapacidade(capacity);
		SalaDAO.getInstance().alterar(oldRoom, newRoom);
	}

	// This method deletes room form the database.
	public void excluir (Sala room) throws SQLException, PatrimonioException {

		SalaDAO.getInstance().excluir(room);
		this.roomsVec.remove(room);
	}

}
