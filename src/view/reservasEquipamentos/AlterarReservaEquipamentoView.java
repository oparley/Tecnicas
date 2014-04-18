/**
EquipmentModifyReservationView
This class allows a user to modify a equipmentToReserveuipment instanceOfReservetion
https://github.com/ParleyMartins/Tecnicas/tree/estiloDesign/src/view/instanceOfReservesEquipamentos
 */

package view.reservasEquipamentos;

import java.awt.Color;
import java.awt.Frame;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import model.Equipamento;
import model.ReservaEquipamentoProfessor;
import exception.ClienteException;
import exception.PatrimonioException;
import exception.ReservaException;

public class AlterarReservaEquipamentoView extends ReservaEquipamentoView {

	// int index;
	ReservaEquipamentoProfessor instanceOfReserve;
	Equipamento instanceOfEquipment;

	public AlterarReservaEquipamentoView (Frame parent, boolean modal,
			int index, int month, Equipamento equipmentToReserve) throws SQLException,
			PatrimonioException, ClienteException, ReservaException {

		super(parent, modal);
		this.instanceOfEquipment = equipmentToReserve;
		// this.index = index;
		this.instanceOfReserve = this.instanceManageResEquipmentTeacher.getReservationsPerMonth(month).get(index);
		resetComponents();
	}

	@Override
	protected void reserveEquipmentToTeacher ( ) {

		try {
			this.instanceManageResEquipmentTeacher.modify(this.instanceOfReserve,
					this.dateTextField.getText(), this.hourTextField.getText(),
					this.instanceOfEquipment, this.instanceOfReserve.getProfessor());
			JOptionPane.showMessageDialog(this, "Reserva alterada com sucesso",
					"Sucesso", JOptionPane.INFORMATION_MESSAGE, null);
			this.setVisible(false);
		} catch (ReservaException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro",
					JOptionPane.ERROR_MESSAGE, null);
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(),
					"Erro", JOptionPane.ERROR_MESSAGE, null);
		}
	}

	private void resetComponents ( ) {

		this.reserveButton.setText("Alterar");
		this.reserveButton.setName("AlterarButton");
		this.cpfLabel.setEnabled(false);
		this.cpfTextField.setBackground(new Color(200, 208, 254));
		this.cpfTextField.setEditable(false);
		this.cpfTextField.setText(this.instanceOfReserve.getProfessor().getCpf());
		this.hourTextField.setText(this.instanceOfReserve.getHora());
		this.dateTextField.setText(this.instanceOfReserve.getData());
		this.instanceTeacherTextArea.setText(this.instanceOfReserve.getProfessor().toString());
		this.equipmentTextArea.setText(this.instanceOfReserve.getEquipamento()
				.toString());
	}

}
