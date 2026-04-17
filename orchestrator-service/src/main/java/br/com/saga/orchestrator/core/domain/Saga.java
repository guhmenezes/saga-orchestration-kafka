package br.com.saga.orchestrator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "sagas")
public class Saga {

    @Id
    private String id;

    @Indexed(unique = true)
    private String orderId;

    private SagaStatus status;

    @Builder.Default
    private List<SagaStepLog> history = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime finishedAt;

    public void addHistory(String step, String details) {
        if (this.history == null) {
            this.history = new ArrayList<>();
        }

        this.history.add(new SagaStepLog(step, details, LocalDateTime.now()));
    }
}
