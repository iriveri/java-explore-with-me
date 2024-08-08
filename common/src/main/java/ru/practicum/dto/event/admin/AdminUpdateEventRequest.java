package ru.practicum.dto.event.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.EventUpdateRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminUpdateEventRequest extends EventUpdateRequest {
    AdminAction stateAction;

    @Override
    public AdminAction getStateAction() {
        return stateAction;
    }
}
