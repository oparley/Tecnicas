/**
ResSalaProfessorDAO
Manage DAO relations between ReservaSala and Professor
https://github.com/ParleyMartins/Tecnicas/blob/estiloDesign/src/persistence/
*/

package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import view.International;
import model.Professor;
import model.ReservaSalaProfessor;
import model.Sala;
import exception.ClienteException;
import exception.PatrimonioException;
import exception.ReservaException;

public class ResSalaProfessorDAO extends DAO {

	// Excpetion messages and alerts.
	private final String NULL = International.getInstance().getMessages().getString("null");
	private final String ROOM_UNAVAILABLE = International.getInstance().getMessages().getString("roomUnavailable");
	private final String TEACHER_INEXISTENT = International.getInstance().getMessages().getString("teacherInexistent");
	private final String ROOM_INEXISTENT = International.getInstance().getMessages().getString("roomInexistent");
	private final String RESERVATION_INEXISTENT = International.getInstance().getMessages().getString("reservationInexistent");
	private final String RESERVATION_EXISTENT = International.getInstance().getMessages().getString("reservationExistent");
	private final String DATE_IS_GONE = International.getInstance().getMessages().getString("dateIsGone");
	private final String TIME_IS_GONE = International.getInstance().getMessages().getString("timeIsGone");

	// Singleton implementation.
	private static ResSalaProfessorDAO instance;

	private ResSalaProfessorDAO ( ) {

		// Blank constructor.
	}

	public static ResSalaProfessorDAO getInstance ( ) {

		if (instance == null) {
			instance = new ResSalaProfessorDAO();
		} else {
			// Nothing here.
		}
		return instance;
	}

	

	// Include a new entry in the database.
	public void insert (ReservaSalaProfessor reservation) throws ReservaException,
			SQLException {

		if (reservation == null) {
			throw new ReservaException(NULL);
		} else {
			if (!this.teacherIsInDB(reservation.getTeacher())) {
				throw new ReservaException(TEACHER_INEXISTENT);
			} else {
				if (!this.roomIsInDB(reservation.getClassroom())) {
					throw new ReservaException(ROOM_INEXISTENT);
				} else {
					if (this.roomIsInReservationDB(reservation.getClassroom(), reservation.getDate(), 
							reservation.getTime())) {
						throw new ReservaException(ROOM_UNAVAILABLE);
					} else {
						if (this.reservationIsInDB(reservation)) {
							throw new ReservaException(RESERVATION_EXISTENT);
						} else {
							if (this.roomIsInReservationDB(reservation.getDate(), 
									reservation.getTime())) {
								super.execute(this.deleteFromStudentQuery(reservation));
							} else {
								// Nothing here.
							}
						}
					}
				}
			}
		}
		if (this.dateIsGone(reservation.getDate())) {
			throw new ReservaException(DATE_IS_GONE);
		} else {
			// Nothing here.
		}
		if (this.dataIsNow(reservation.getDate())) {
			if (this.timeIsGone(reservation.getTime())) {
				throw new ReservaException(TIME_IS_GONE);
			} else {
				super.execute(this.insertIntoQuery(reservation));
			}
		} else {
			super.execute(this.insertIntoQuery(reservation));
		}
	}

	// Change an entry in the database.
	public void modify (ReservaSalaProfessor oldReservation, ReservaSalaProfessor newReservation)
			throws ReservaException, SQLException {

		if (oldReservation == null) {
			throw new ReservaException(NULL);
		} else {
			if (newReservation == null) {
				throw new ReservaException(NULL);
			} else {
				if (!this.reservationIsInDB(oldReservation)) {
					throw new ReservaException(RESERVATION_INEXISTENT);
				} else {
					if (this.reservationIsInDB(newReservation)) {
						throw new ReservaException(RESERVATION_EXISTENT);
					} else {
						if (!this.teacherIsInDB(newReservation.getTeacher())) {
							throw new ReservaException(TEACHER_INEXISTENT);
						} else {
							if (!this.roomIsInDB(newReservation.getClassroom())) {
								throw new ReservaException(ROOM_INEXISTENT);
							} else {
								if (!oldReservation.getDate().equals(newReservation.getDate())
										|| !oldReservation.getTime().equals(newReservation.getTime())) {
									if (this.roomIsInReservationDB(newReservation.getClassroom(),
											newReservation.getDate(), newReservation.getTime())) {
										throw new ReservaException(
												ROOM_UNAVAILABLE);
									} else {
										// Nothing here.
									}
								} else {
									// Nothing here.
								}
							}
						}
					}
				}
			}
		}
								
		if (this.dateIsGone(newReservation.getDate())) {
			throw new ReservaException(DATE_IS_GONE);
		} else {
			// Nothing here.
		}
		if (this.timeIsGone(newReservation.getTime()) && this.dataIsNow(newReservation.getDate())) {
			throw new ReservaException(TIME_IS_GONE);
		} else {
			super.update(this.updateQuery(oldReservation, newReservation));
		}
	}

	// Remove an entry from the database.
	public void delete (ReservaSalaProfessor reservation) throws ReservaException,
			SQLException {

		if (reservation == null) {
			throw new ReservaException(NULL);
		} else {
			if (!this.reservationIsInDB(reservation)) {
				throw new ReservaException(RESERVATION_INEXISTENT);
			} else {
				super.execute(this.deleteFromTeacherQuery(reservation));
			}
		}
	}

	// Select all entries from the database.
	@SuppressWarnings ("unchecked")
	public Vector <ReservaSalaProfessor> searchAll ( ) throws SQLException,
			ClienteException, PatrimonioException, ReservaException {

		return super
				.search("SELECT * FROM reserva_sala_professor "
						+
						"INNER JOIN sala ON sala.id_sala = reserva_sala_professor.id_sala "
						+
						"INNER JOIN professor ON professor.id_professor = reserva_sala_professor.id_professor;");
	}

	// Select entries from the database by date.
	@SuppressWarnings ("unchecked")
	public Vector <ReservaSalaProfessor> searchByDate (String date)
			throws SQLException, ClienteException, PatrimonioException,
			ReservaException {

		return super
				.search("SELECT * FROM reserva_sala_professor "
						+
						"INNER JOIN sala ON sala.id_sala = reserva_sala_professor.id_sala "
						+
						"INNER JOIN professor ON professor.id_professor = reserva_sala_professor.id_professor"
						+
						" WHERE data = \"" + this.standardizeDate(date) + "\";");
	}

	// Fetch an entry using a string.
	@Override
	protected Object fetch (ResultSet result) throws SQLException,
			ClienteException, PatrimonioException, ReservaException {

		Professor teacher = new Professor(result.getString("nome"), result.getString("cpf"),
				result.getString("matricula"),
				result.getString("telefone"), result.getString("email"));

		Sala room = new Sala(result.getString("codigo"), result.getString("descricao"),
				result.getString("capacidade"));

		ReservaSalaProfessor reservation = new ReservaSalaProfessor(result.getString("data"),
				result.getString("hora"),
				room, result.getString("finalidade"), teacher);

		return reservation;
	}

	// Check if there is a Professor in the database.
	private boolean teacherIsInDB (Professor teacher) throws SQLException {

		return super.isInDBGeneric("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + teacher.getName() + "\" and " +
				"professor.cpf = \"" + teacher.getCpf() + "\" and " +
				"professor.telefone = \"" + teacher.getPhoneNumber() + "\" and "
				+
				"professor.email = \"" + teacher.getEmail() + "\" and " +
				"professor.matricula = \"" + teacher.getEnrollmentNumber() + "\";");
	}

	// Check if there is a Sala in the database.
	private boolean roomIsInDB (Sala room) throws SQLException {

		return super.isInDBGeneric("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + room.getIdCode() + "\" and " +
				"sala.descricao = \"" + room.getDescription() + "\" and " +
				"sala.capacidade = " + room.getCapacity() +
				";");
	}

	// Check if there is a Sala entry in a Reserva.
	private boolean roomIsInReservationDB (Sala room, String date, String time)
			throws SQLException {

		return super.isInDBGeneric("SELECT * FROM reserva_sala_professor WHERE " +
				"data = \"" + date + "\" and " +
				"hora = \"" + time + "\" and " +
				"id_sala = (SELECT id_sala FROM sala WHERE " +
				"sala.codigo = \"" + room.getIdCode() + "\" and " +
				"sala.descricao = \"" + room.getDescription() + "\" and " +
				"sala.capacidade = " + room.getCapacity() + " );");
	}

	// Check if there is a Reserva in the database.
	private boolean reservationIsInDB (ReservaSalaProfessor reservation) throws SQLException {

		return super.isInDBGeneric("SELECT * FROM reserva_sala_professor WHERE " +
				"id_professor = (SELECT id_professor FROM professor WHERE " +
				"professor.nome = \"" + reservation.getTeacher().getName() + "\" and " +
				"professor.cpf = \"" + reservation.getTeacher().getCpf() + "\" and " +
				"professor.telefone = \"" + reservation.getTeacher().getPhoneNumber()
				+ "\" and " +
				"professor.email = \"" + reservation.getTeacher().getEmail()
				+ "\" and " +
				"professor.matricula = \"" + reservation.getTeacher().getEnrollmentNumber()
				+ "\") and " +
				"id_sala = (SELECT id_sala FROM sala WHERE " +
				"sala.codigo = \"" + reservation.getClassroom().getIdCode() + "\" and " +
				"sala.descricao = \"" + reservation.getClassroom().getDescription() + "\" and " +
				"sala.capacidade = " + reservation.getClassroom().getCapacity() + " ) and " +
				"finalidade = \"" + reservation.getPurpose() + "\" and " +
				"hora = \"" + reservation.getTime() + "\" and " +
				"data = \"" + reservation.getDate() + "\";");
	}

	// Check if there is an Aluno entry in a Reserva.
	private boolean roomIsInReservationDB (String date, String time)
			throws SQLException {

		return super.isInDBGeneric("SELECT * FROM reserva_sala_aluno WHERE " +
				"data = \"" + date + "\" and " +
				"hora = \"" + time + "\";");
	}

	// Get the current date.
	private String currentDate ( ) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	// Get the current time.
	private String currentTime ( ) {

		Date date = new Date(System.currentTimeMillis());
		return date.toString().substring(11, 16);
	}

	// Check if the date is passed.
	private boolean dateIsGone (String date) {

		String now[] = this.currentDate().split("[./-]");
		String dateParts[] = date.split("[./-]");

		int differenceDates = now[2].length() - dateParts[2].length();
		dateParts[2] = now[2].substring(0, differenceDates) + dateParts[2];

		if (Integer.parseInt(now[2]) > Integer.parseInt(dateParts[2])) {
			return true;
		} else {
			// Nothing here.
		}

		differenceDates = now[1].length() - dateParts[1].length();
		dateParts[1] = now[1].substring(0, differenceDates) + dateParts[1];

		if (Integer.parseInt(now[1]) > Integer.parseInt(dateParts[1])) {
			return true;
		} else {
			if (Integer.parseInt(now[1]) == Integer.parseInt(dateParts[1])) {
				differenceDates = now[0].length() - dateParts[0].length();
				dateParts[0] = now[0].substring(0, differenceDates) + dateParts[0];

				if (Integer.parseInt(now[0]) > Integer.parseInt(dateParts[0])) {
					return true;
				} else {
					// Nothing here.
				}
			} else {
				// Nothing here.
			}
		}
		return false;
	}

	// Check if the date is equals.

	public boolean dataIsNow (String date) {

		date = this.standardizeDate(date);
		String now[] = this.currentDate().split("[./-]");
		String dateParts[] = date.split("[./-]");

		if (now[0].equals(dateParts[0]) && now[1].equals(dateParts[1])
				&& now[2].equals(dateParts[2])) {
			return true;
		} else {
			// Nothing here.
		}
		return false;
	}

	// Check if the time is passed.

	private boolean timeIsGone (String time) {

		String now = this.currentTime();
		if (time.length() == 4) {
			time = "0" + time;
		} else {
			// Nothing here.
		}
		if (Integer.parseInt(now.substring(0, 2)) > Integer.parseInt(time
				.substring(0, 2))) {
			return true;
		} else {
			if (Integer.parseInt(now.substring(0, 2)) == Integer
					.parseInt(time.substring(0, 2))) {
				if (Integer.parseInt(now.substring(3, 5)) > Integer
						.parseInt(time.substring(3, 5))) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	// Standardize the date.
	
	private String standardizeDate (String date) {

		String now[] = currentDate().split("[./-]");
		String dateParts[] = date.split("[./-]");
		String standardDate = "";

		for (int i = 0 ; i < 3 ; i++) {
			if (i == 0) {
				standardDate += now[i].substring(0,
						now[i].length() - dateParts[i].length()) + dateParts[i];
			} else {
				standardDate += "/" + now[i].substring(0,
						now[i].length() - dateParts[i].length()) + dateParts[i];
			}
		}

		return standardDate;
	}

	// Reuse query for SELECT PROFESSOR BY ID clause.

	private String selectTeacherIDQuery (Professor teacher) {

		return "SELECT id_professor FROM professor WHERE " +
				"professor.nome = \"" + teacher.getName() + "\" and " +
				"professor.cpf = \"" + teacher.getCpf() + "\" and " +
				"professor.telefone = \"" + teacher.getPhoneNumber() + "\" and " +
				"professor.email = \"" + teacher.getEmail() + "\" and " +
				"professor.matricula = \"" + teacher.getEnrollmentNumber() + "\"";
	}

	// Reuse query for SELECT SALA BY ID clause.

	private String selectRoomIdQuery (Sala room) {

		return "SELECT id_sala FROM sala WHERE " +
				"sala.codigo = \"" + room.getIdCode() + "\" and " +
				"sala.descricao = \"" + room.getDescription() + "\" and " +
				"sala.capacidade = " + room.getCapacity();
	}

	// Reuse Query for WHERE clause.

	private String whereQuery (ReservaSalaProfessor reservation) {

		return " WHERE " +
				"id_professor = ( " + selectTeacherIDQuery(reservation.getTeacher())
				+ " ) and " +
				"id_sala = ( " + selectRoomIdQuery(reservation.getClassroom()) + " ) and " +
				"finalidade = \"" + reservation.getPurpose() + "\" and " +
				"hora = \"" + reservation.getTime() + "\" and " +
				"data = \"" + reservation.getDate() + "\"";
	}

	// Reuse Query for VALUES clause.

	private String valuesQuery (ReservaSalaProfessor reservation) {

		return "( " + selectTeacherIDQuery(reservation.getTeacher()) + " ), " +
				"( " + selectRoomIdQuery(reservation.getClassroom()) + " ), " +
				"\"" + reservation.getPurpose() + "\", " +
				"\"" + reservation.getTime() + "\", " +
				"\"" + reservation.getDate() + "\"";
	}

	// Reuse Query for ATRIBUTES clause.
	
	private String attributesQuery (ReservaSalaProfessor reservation) {

		return "id_professor = ( " + selectTeacherIDQuery(reservation.getTeacher())
				+ " ), " +
				"id_sala = ( " + selectRoomIdQuery(reservation.getClassroom()) + " ), " +
				"finalidade = \"" + reservation.getPurpose() + "\", " +
				"hora = \"" + reservation.getTime() + "\", " +
				"data = \"" + reservation.getDate() + "\"";
	}

	// Reuse Query for INSERT clause.

	private String insertIntoQuery (ReservaSalaProfessor reservation) {

		return "INSERT INTO "
				+
				"reserva_sala_professor (id_professor, id_sala, finalidade, hora, data) "
				+
				"VALUES ( " + valuesQuery(reservation) + " );";
	}

	// Reuse Query for DELETE PROFESSOR clause.

	private String deleteFromTeacherQuery (ReservaSalaProfessor reservation) {

		return "DELETE FROM reserva_sala_professor "
				+ this.whereQuery(reservation) + " ;";
	}

	// Reuse Query for DELETE ALUNO clause.

	private String deleteFromStudentQuery (ReservaSalaProfessor reservation) {

		return "DELETE FROM reserva_sala_aluno WHERE " +
				"hora = \"" + reservation.getTime() + "\" and " +
				"data = \"" + reservation.getDate() + "\" ;";
	}

	// Reuse Query for UPDATE clause.

	private String updateQuery (ReservaSalaProfessor oldReservation, ReservaSalaProfessor newReservation) {

		return "UPDATE reserva_sala_professor SET " +
				this.attributesQuery(newReservation) +
				this.whereQuery(oldReservation) + " ;";
	}
}