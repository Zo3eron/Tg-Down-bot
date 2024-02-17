package com.company.Teksi.bot;


import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = ("users"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Long chatId;
    //ismi va Telefon nomeri
    private String msg;
    //qaysi bosqichda ekanligi
    private String step;
    //Tili
    private String language;
    // yolovchimi yoki haydovchiligini
    private String type;
    //necha kishiligi yoki pochtani
    private String count;
    //qayerdan qayergaligi
    private String location;
    //user nicke
    private String username;
    private String firstName;
}
