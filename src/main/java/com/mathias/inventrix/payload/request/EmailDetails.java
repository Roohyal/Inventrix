package com.mathias.inventrix.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {
     private String fullName;

     private String companyName;

     private String companyId;

     private String recipient;

     private  String password;

     private String messageBody;

     private String subject;

     private String attachment;

     private String link;
}
