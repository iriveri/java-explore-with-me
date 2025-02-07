package ru.practicum.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.admin.AdminAction;
import ru.practicum.dto.event.admin.AdminUpdateEventRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateCommand;
import ru.practicum.dto.requests.EventRequestStatusUpdateResponse;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.dto.requests.RequestStatus;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.request.ParticipationRequestMapper;
import ru.practicum.request.ParticipationRequestRepository;
import ru.practicum.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional // Обеспечивает откат транзакций после каждого теста
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ParticipationRequestServiceIntegrationTest {

    @Autowired
    private ParticipationRequestService participationRequestService;

    @Autowired
    private EventService eventService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ParticipationRequestRepository participationRequestRepository;

    @Autowired
    private ParticipationRequestMapper participationRequestMapper;

    private Long partisipantId;
    private Long eventId;
    private Long userId;

    @BeforeEach
    public void setUp() {
        userId = userService.create(new NewUserDto("just.bob@bobs.ru", "MR.BOBS")).getId();
        Long categoryId = categoryService.create(new NewCategoryDto("Pizza")).getId();

        partisipantId = userService.create(new NewUserDto("just.mob@bobs.ru", "MR.MOMS")).getId();

        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setTitle("Test Event");
        newEventDto.setCategory(categoryId);
        newEventDto.setParticipantLimit(1);
        newEventDto.setEventDate(LocalDateTime.now().plusHours(3)); // Устанавливаем дату события через 3 часа

        eventId = eventService.create(userId, newEventDto).getId();
        var updatedto = new AdminUpdateEventRequest();
        updatedto.setStateAction(AdminAction.PUBLISH_EVENT);
        eventService.update(eventId, updatedto);
    }

    @Test
    public void testCreateParticipationRequest() {
        ParticipationRequestDto requestDto = participationRequestService.create(partisipantId, eventId);

        assertThat(requestDto).isNotNull();
        assertThat(requestDto.getRequester()).isEqualTo(partisipantId);
        assertThat(requestDto.getEvent()).isEqualTo(eventId);
        assertThat(requestDto.getStatus()).isEqualTo(RequestStatus.PENDING); // Ожидаем статус PENDING при модерации
    }

    @Test
    public void testCreateParticipationRequest_UserAlreadyParticipating() {
        participationRequestService.create(partisipantId, eventId); // Первый запрос

        ConditionNotMetException exception = assertThrows(
                ConditionNotMetException.class,
                () -> participationRequestService.create(partisipantId, eventId)
        );

        assertThat(exception.getMessage()).isEqualTo("Participation request already exists.");
    }

    @Test
    public void testUpdateStatus() {
        ParticipationRequestDto requestDto = participationRequestService.create(partisipantId, eventId);

        EventRequestStatusUpdateCommand updateRequest = new EventRequestStatusUpdateCommand();
        updateRequest.setRequestIds(Arrays.asList(requestDto.getId()));
        updateRequest.setStatus(RequestStatus.CONFIRMED);

        EventRequestStatusUpdateResponse result = participationRequestService.updateStatus(userId, eventId, updateRequest);

        assertThat(result.getConfirmedRequests()).hasSize(1);
        assertThat(result.getConfirmedRequests().get(0).getStatus()).isEqualTo(RequestStatus.CONFIRMED);
    }

    @Test
    public void testUpdateStatus_NonInitiator() {
        Long anotherUserId = userService.create(new NewUserDto("just.bobs@bobs.ru", "MRS.BOBS")).getId();

        ParticipationRequestDto requestDto = participationRequestService.create(anotherUserId, eventId);

        EventRequestStatusUpdateCommand updateRequest = new EventRequestStatusUpdateCommand();
        updateRequest.setRequestIds(Arrays.asList(requestDto.getId()));
        updateRequest.setStatus(RequestStatus.CONFIRMED);

        ConditionNotMetException exception = assertThrows(
                ConditionNotMetException.class,
                () -> participationRequestService.updateStatus(partisipantId, eventId, updateRequest)
        );

        assertThat(exception.getMessage()).isEqualTo("Only event initiator can update request status.");
    }
}