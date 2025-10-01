package me.lyamray.bnsmpcore.data.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendsData {

    private UUID playerUuid;
    private UUID friendUuid;

}