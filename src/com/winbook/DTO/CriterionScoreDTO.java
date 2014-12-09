package com.winbook.DTO;

import org.springframework.jdbc.core.JdbcTemplate;

public class CriterionScoreDTO {

	//Need to add 'revision' for optimistic locking
		private int itemId=0, criterionId=0, score=0, revision=1;
		public int getItemId() {
			return itemId;
		}

		public void setItemId(int itemId) {
			this.itemId = itemId;
		}

		public int getCriterionId() {
			return criterionId;
		}

		public void setCriterionId(int criterionId) {
			this.criterionId = criterionId;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public int getRevision() {
			return revision;
		}

		public void setRevision(int revision) {
			this.revision = revision;
		}

		public void nextRevision(){
			this.revision++;
		}
	
}
