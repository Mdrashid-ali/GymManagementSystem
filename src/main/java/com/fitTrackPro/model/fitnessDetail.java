package com.fitTrackPro.model;

import java.sql.Timestamp;

/**
 * Stores one member fitness progress entry.
 */
public class fitnessDetail {
	private int detailId;
	private int memberId;
	private Double heightCm;
	private Double weightKg;
	private Double bodyFatPercent;
	private Double muscleMassKg;
	private String fitnessGoal;
	private String notes;
	private Timestamp recordedAt;

	public int getDetailId() {
		return detailId;
	}

	public void setDetailId(int detailId) {
		this.detailId = detailId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public Double getHeightCm() {
		return heightCm;
	}

	public void setHeightCm(Double heightCm) {
		this.heightCm = heightCm;
	}

	public Double getWeightKg() {
		return weightKg;
	}

	public void setWeightKg(Double weightKg) {
		this.weightKg = weightKg;
	}

	public Double getBodyFatPercent() {
		return bodyFatPercent;
	}

	public void setBodyFatPercent(Double bodyFatPercent) {
		this.bodyFatPercent = bodyFatPercent;
	}

	public Double getMuscleMassKg() {
		return muscleMassKg;
	}

	public void setMuscleMassKg(Double muscleMassKg) {
		this.muscleMassKg = muscleMassKg;
	}

	public String getFitnessGoal() {
		return fitnessGoal;
	}

	public void setFitnessGoal(String fitnessGoal) {
		this.fitnessGoal = fitnessGoal;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Timestamp getRecordedAt() {
		return recordedAt;
	}

	public void setRecordedAt(Timestamp recordedAt) {
		this.recordedAt = recordedAt;
	}

	public Double getBmi() {
		if (heightCm == null || weightKg == null || heightCm <= 0)
			return null;
		double heightM = heightCm / 100.0;
		return weightKg / (heightM * heightM);
	}
}
