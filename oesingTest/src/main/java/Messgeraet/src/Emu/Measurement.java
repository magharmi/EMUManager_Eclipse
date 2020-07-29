package Messgeraet.src.Emu;

public class Measurement {
	String power, amperage, voltage, inductiveReactivePower, capacitiveReactivePower;
	
	public Measurement(String power, String amperage, String voltage, String inductiveReactivePower, String capacitiveReactivePower) {
		setPower(power);
		setAmperage(amperage);
		setVoltage(voltage);
		setInductiveReactivePower(inductiveReactivePower);
		setCapacitiveReactivePower(capacitiveReactivePower);
	}
	
	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getAmperage() {
		return amperage;
	}

	public void setAmperage(String amperage) {
		this.amperage = amperage;
	}

	public String getVoltage() {
		return voltage;
	}

	public void setVoltage(String voltage) {
		this.voltage = voltage;
	}

	public String getCapacitiveReactivePower() { return capacitiveReactivePower; }

	public void setCapacitiveReactivePower(String capacitiveReactivePower) { this.capacitiveReactivePower = capacitiveReactivePower; }

	public String getInductiveReactivePower() { return inductiveReactivePower; }

	public void setInductiveReactivePower(String inductiveReactivePower) { this.inductiveReactivePower = inductiveReactivePower; }

}
