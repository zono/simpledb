package simpleclient.embedded;

import java.sql.*;

import simpledb.jdbc.embedded.EmbeddedDriver;

public class CreateStudentDB {
  public static void main(String[] args) {
    Driver d = new EmbeddedDriver();
    String url = "jdbc:simpledb:studentdb";

    try (Connection conn = d.connect(url, null);
        Statement stmt = conn.createStatement()) {
      String s = "create table STUDENT(SId int, SName varchar(10), MajorId int, GradYear int)";
      stmt.executeUpdate(s);
      System.out.println("Table STUDENT created.");

      s = "insert into STUDENT(SId, SName, MajorId, GradYear) values ";
      String[] studvals = { "(1, 'joe', 10, 2021)",
          "(2, 'amy', 20, 2020)",
          "(3, 'max', 10, 2022)",
          "(4, 'sue', 20, 2022)",
          "(5, 'bob', 30, 2022)",
          "(6, 'kim', 20, 2020)",
          "(7, 'art', 30, 2021)",
          "(8, 'pat', 20, 2019)",
          "(9, 'lee', 10, 2021)" };
      for (int i = 0; i < studvals.length; i++)
        stmt.executeUpdate(s + studvals[i]);
      System.out.println("STUDENT records inserted.");

      s = "create table DEPT(DId int, DName varchar(8))";
      stmt.executeUpdate(s);
      System.out.println("Table DEPT created.");

      s = "insert into DEPT(DId, DName) values ";
      String[] deptvals = {
          "(10, 'compsci')",
          "(20, 'math')",
          "(30, 'drama')"
      };

      for (int i = 0; i < deptvals.length; i++)
        stmt.executeUpdate(s + deptvals[i]);
      System.out.println("DEPT records inserted.");

      s = "create table COURSE(CId int, Title varchar(20), DeptId int)";
      stmt.executeUpdate(s);
      System.out.println("Table COURSE created.");

      s = "insert into COURSE(CId, Title, DeptId) values ";
    }
  }
}
