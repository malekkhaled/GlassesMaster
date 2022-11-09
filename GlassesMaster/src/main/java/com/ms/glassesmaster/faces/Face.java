package com.ms.glassesmaster.faces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Face {
	
	@JsonProperty
	private String faceId;
	@JsonProperty
	private Rectangle faceRectangle;
	@JsonProperty
	private Attributes faceAttributes;
	@JsonProperty
	private Landmarks faceLandmarks;
	
	// Constructor
	public Face() {}
	
	// Getters/Setters
	public String getFaceId() { return faceId; }
	public void setFaceId(String faceId) { this.faceId = faceId; }

	public Rectangle getFaceRectangle() { return faceRectangle; }
	public void setFaceRectangle(Rectangle faceRectangle) { this.faceRectangle = faceRectangle; }

	public Attributes getFaceAttributes() { return faceAttributes; }
	public void setFaceAttributes(Attributes faceAttributes) { this.faceAttributes = faceAttributes; }

	public Landmarks getFaceLandmarks() { return faceLandmarks; }
	public void setFaceLandmarks(Landmarks faceLandmarks) { this.faceLandmarks = faceLandmarks; }

	
	//////////////////////////////////////////////////////////////////
	// Nested classes (for JSON deserializing)
	//////////////////////////////////////////////////////////////////


	// class: Rectangle
	public class Rectangle {
		
		@JsonProperty
		private Integer width;
		@JsonProperty
		private Integer height;
		@JsonProperty
		private Integer left;
		@JsonProperty
		private Integer top;
		
		// Empty public constructor (required for Jackson JSON deserializing)
		public Rectangle() {}

		// Getters/Setters
		public Integer getWidth() { return width; }
		public void setWidth(Integer width) { this.width = width; }

		public Integer getHeight() { return height; }
		public void setHeight(Integer height) { this.height = height; }

		public Integer getLeft() { return left; }
		public void setLeft(Integer left) { this.left = left; }

		public Integer getTop() { return top; }
		public void setRight(Integer top) { this.top = top; }
	}  // end Rectangle{}
	
	
	// class: Attributes
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class Attributes {
		
		@JsonProperty
		private String glasses;
		@JsonProperty
		private Pose headPose;
		
		// Empty public constructor (required for Jackson JSON deserializing)
		public Attributes() {}
		
		// Getters/Setters
		public String getGlasses() { return glasses; }
		public void setGlasses(String glasses) { this.glasses = glasses; }

		public Pose getHeadPose() { return headPose; }
		public void setHeadPose(Pose headPose) { this.headPose = headPose; }

		// class: Attributes->Pose
		public class Pose {
			
			@JsonProperty
			private Double roll;
			@JsonProperty
			private Double yaw;
			@JsonProperty
			private Double pitch;
			
			// Empty public constructor (required for Jackson JSON deserializing)
			public Pose() {}

			// Getters/Setters
			public Double getRoll() { return roll; }
			public void setRoll(Double roll) { this.roll = roll; }

			public Double getYaw() { return yaw; }
			public void setYaw(Double yaw) { this.yaw = yaw; }

			public Double getPitch() { return pitch; }
			public void setPitch(Double pitch) { this.pitch = pitch; }
		}  // end Pose{}
	}  // end Attributes{}
	
	
	// class: Landmarks
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class Landmarks {
		
		@JsonProperty
		private Coordinates pupilLeft;
		@JsonProperty
		private Coordinates pupilRight;
		
		// Empty public constructor (required for Jackson JSON deserializing)
		public Landmarks() {}
		
		// Getters/Setters
		public Coordinates getPupilLeft() { return pupilLeft; }
		public void setPupilLeft(Coordinates pupilLeft) { this.pupilLeft = pupilLeft; }

		public Coordinates getPupilRight() { return pupilRight; }
		public void setPupilRight(Coordinates pupilRight) { this.pupilRight = pupilRight; }

		// class: Landmarks->Coordinates
		public class Coordinates {
			
			@JsonProperty
			private Double x;
			@JsonProperty
			private Double y;
			
			// Empty public constructor (required for Jackson JSON deserializing)
			public Coordinates() {}

			// Getters/Setters
			public Double getX() { return x; }
			public void setX(Double x) { this.x = x; }

			public Double getY() { return y; }
			public void setY(Double y) { this.y = y; }
		}  // end Coordinates
	}  // end Landmarks{}
}