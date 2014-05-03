/**
ReservaSalaAluno. 
Class sets exceptions of ReservaSalaAluno.
https://github.com/ParleyMartins/Tecnicas/tree/master/src/model/ReservaSalaAluno.java.
*/

package model;

import view.International;
import exception.ReservaException;

public class ReservaSalaAluno extends ReservaSala {

	private Aluno student;
	private String reservedChairs;

	// Error messages.
	private final String BLANK_STUDENT = International.getInstance().getMessages().getString("blankStudent");
	private final String BLANK_CHAIRS = International.getInstance().getMessages().getString("blankNumberOfChairs");
	private final String INVALID_CHAIRS = International.getInstance().getMessages().getString("invalidNumberOfChairs");
	private final String OVER_LIMIT_CHAIRS = International.getInstance().getMessages().getString("outOfRangeNumberOfChairs");
	private final String CHAIRS_PATTERN = "^[\\d]+$";

	public ReservaSalaAluno(String date, String time, Sala classroom,
			String purpose, String reserved_chairs, Aluno student)
			throws ReservaException {

		super(date, time, classroom, purpose);
		this.setStudent(student);
		this.setReservedChairs(reserved_chairs);
	}

	public Aluno getStudent() {

		return this.student;
	}

	public String getReservedChairs() {

		return this.reservedChairs;
	}

	public void setStudent(Aluno student) throws ReservaException {

		if (student == null) {
			throw new ReservaException(BLANK_STUDENT);
		} else {
			// Do nothing.
		}
		this.student = student;
	}

	public void setReservedChairs(String reservedChairs)
			throws ReservaException {

		// So we can modify this value after, without losing the information.
		String chairs = reservedChairs;
		if (chairs == null) {
			throw new ReservaException(BLANK_CHAIRS);
		} else {
			// Do nothing.
		}
		chairs = chairs.trim();
		if (chairs.equals("")) {
			throw new ReservaException(BLANK_CHAIRS);
		} else {
			if (chairs.matches(CHAIRS_PATTERN)) {
				if (Integer.parseInt(super.getClassroom().getCapacity()) < Integer
						.parseInt(reservedChairs)) {
					throw new ReservaException(OVER_LIMIT_CHAIRS);
				} else {
					this.reservedChairs = reservedChairs;
				}
			} else {
				throw new ReservaException(INVALID_CHAIRS);
			}
		}
	}

	public boolean equals(ReservaSalaAluno reservation) {

		return (super.equals(reservation)
				&& this.getStudent().equals(reservation.getStudent()) && this
				.getReservedChairs().equals(
						reservation.getReservedChairs()));
	}

	@Override
	public String toString() {

		return "Aluno: " + this.getStudent().toString()
				+ "\nCadeiras Reservadas: " + this.getReservedChairs()
				+ super.toString();
	}

}
