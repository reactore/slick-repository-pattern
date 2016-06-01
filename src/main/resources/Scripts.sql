CREATE TABLE employee."User"
(
  "UserId" bigserial NOT NULL,
  "UserName" character varying(50) NOT NULL,
  "Email" character varying(50) NOT NULL,
  "Password" character varying(50) NOT NULL,
  "IsDeleted" boolean NOT NULL,
  "EmployeeId" bigint NOT NULL,
  CONSTRAINT "User_pkey" PRIMARY KEY ("UserId")
);

CREATE TABLE employee."Employee"
(
  "EmployeeId" bigserial NOT NULL,
  "FirstName" character varying(50) NOT NULL,
  "LastName" character varying(50) NOT NULL,
  "DOB" date NOT NULL,
  "IsDeleted" boolean NOT NULL,
  CONSTRAINT "Employee_pkey" PRIMARY KEY ("EmployeeId")
);
