/**
 * ManterResEquipamentoProfessor 
 * Controller of the relation between equipment and teacher. Include the 
 * procedures to access, modify, and delete this kind of reservations. In this 
 * class, we use Singleton to guarantee just one instance at time, since this 
 * is a MVC controller. To execute the described actions, this class need to 
 * communicate with the DAO layer.
 * https://github.com/ParleyMartins/Tecnicas/tree/master/src/control/ManterResEquipamentoProfessor.java
 */
package control;

import java.sql.SQLException;
import java.util.Vector;

import model.Equipamento;
import model.Professor;
import model.ReservaEquipamentoProfessor;
import persistence.ResEquipamentoProfessorDAO;
import exception.ClienteException;
import exception.PatrimonioException;
import exception.ReservaException;

public class ManterResEquipamentoProfessor {

	private Vector<Object> teacherEquipReservationVector = new Vector<Object>();

	private static ManterResEquipamentoProfessor instance;
	private static ResEquipamentoProfessorDAO resDAOInstance;

	/*
	 * Private constructor, to guarantee the use via singleton.
	 */
	private ManterResEquipamentoProfessor() {

		// Blank constructor.
	}

	/**
	 * Provides the singleton implementation
	 * @return the active ManterResEquipamentoProfessor instance, since it will
	 * be just one at time
	 */
	public static ManterResEquipamentoProfessor getInstance() {

		if (instance == null) {
			instance = new ManterResEquipamentoProfessor();
			resDAOInstance = ResEquipamentoProfessorDAO.getInstance();
		} else {
			// Nothing here.
		}
		return instance;
	}

	/**
	 * Provides the equipment reservation made by a teacher in a period of time
	 * @param time period of interest (HH:MM)
	 * @return a Vector with all equipments reserved at the period
	 * @throws SQLException If has some problem with the database search
	 * @throws PatrimonioException If some of the equipment info is invalid
	 * @throws ClienteException If some of the teacher info is invalid
	 * @throws ReservaException If some of the equipment info is invalid
	 */
	public Vector<ReservaEquipamentoProfessor> getReservationsPerTime(
			String time) throws SQLException, PatrimonioException,
			ClienteException, ReservaException {

		Vector<ReservaEquipamentoProfessor> reservations = resDAOInstance.searchByTime(time);
		return reservations;
	}

	/**
	 * Provides the equipment reservation made by a teacher in a month period.
	 * @param month period of interest (dd/mm/YYYY)
	 * @return a Vector with all equipments reserved at the period
	 * @throws SQLException If has some problem with the database search
	 * @throws PatrimonioException If some of the equipment info is invalid
	 * @throws ClienteException If some of the teacher info is invalid
	 * @throws ReservaException If some of the equipment info is invalid
	 */
	public Vector<ReservaEquipamentoProfessor> getReservationsPerMonth(int month)
			throws SQLException, PatrimonioException, ClienteException,
			ReservaException {
		
		Vector<ReservaEquipamentoProfessor> reservations = resDAOInstance.searchByMonth(month); 
		return reservations;
	}

	/**
	 * Provides all equipments reservations made by teachers
	 * @return a Vector with all equipments reservations made by teachers
	 * @throws SQLException If has some problem with the database search
	 * @throws ClienteException If some of the teacher info is invalid
	 * @throws PatrimonioException If some of the equipment info is invalid
	 * @throws ReservaException If some of the equipment info is invalid
	 */
	public Vector<Object> getTeacherEquipReservationVector()
			throws SQLException, ClienteException, PatrimonioException,
			ReservaException {

		this.teacherEquipReservationVector = resDAOInstance.searchAll();
		return this.teacherEquipReservationVector;
	}

	/**
	 * Insert a new reservation in the database
	 * @param equipment equipment to be reserved
	 * @param teacher teacher that will make the reserve
	 * @param date reservation date (dd/mm/YYYY)
	 * @param time reservation time (HH:MM)
	 * @throws SQLException If has some problem with the database insertion
	 * @throws ReservaException If some of the equipment info is invalid
	 */
	public void insert(Equipamento equipment, Professor teacher, String date,
			String time) throws SQLException, ReservaException {

		ReservaEquipamentoProfessor reserva = new ReservaEquipamentoProfessor(
				date, time, equipment, teacher);
		resDAOInstance.insert(reserva);
		this.teacherEquipReservationVector.add(reserva);
	}

	/**
	 * Modify reservation of a equipment in the database
	 * @param oldReservation reservation to be updated
	 * @param date new reservation date (dd/mm/YYYY)
	 * @param time new reservation time (HH:MM)
	 * @param equipment new equipment to be reserved
	 * @param teacher new teacher that will make the reserve
	 * @throws SQLException If has some problem with the database update
	 * @throws ReservaException If some of the equipment info is invalid
	 */
	public void modify(ReservaEquipamentoProfessor oldReservation, String date,
			String time, Equipamento equipment, Professor teacher)
			throws SQLException, ReservaException {

		ReservaEquipamentoProfessor reserva_new = new ReservaEquipamentoProfessor(
				date, time, equipment, teacher);

		resDAOInstance.modify(oldReservation,
				reserva_new);

	}

	/**
	 * Remove a reservation made by a teacher from the database
	 * @param reservation reservation to be removed
	 * @throws SQLException If has some problem with the database deletion
	 * @throws ReservaException If some of the equipment info is invalid
	 */
	public void delete(ReservaEquipamentoProfessor reservation)
			throws SQLException, ReservaException {

		resDAOInstance.delete(reservation);
		this.teacherEquipReservationVector.remove(reservation);
	}
}
