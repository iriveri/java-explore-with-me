package ru.practicum.dto.event.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.EventUpdateRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserUpdateEventRequest extends EventUpdateRequest {
    UserAction stateAction;

    @Override
    public UserAction getStateAction() {
        return stateAction;
    }
}
