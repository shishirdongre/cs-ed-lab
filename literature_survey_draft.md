# Literature Survey — First Draft (Bullet Points)

## 1. Machine Learning Education

### K–12 ML Education
- Shift from rule-based to data-driven thinking; focus on curating, creating, labeling, and feeding training data rather than hand-coding rules (Tedre et al., Teaching ML in K-12)
- Tools: Google Teachable Machine, IBM Watson-based Machine Learning for Kids, Wolfram Programming Lab, Cognimates, eCraft2Learn, MIT App Inventor extensions (Tedre et al.)
- AI4K12 “five big ideas”: perception through sensors, reasoning through models, learning from examples, interacting with people, societal impact (Tedre et al.)
- Design-oriented pedagogy: children as co-designers; middle-schoolers build ML apps for meaningful problems (Vartiainen et al., Machine learning for middle-schoolers)
- Children learn ML concepts through bodily interaction (poses, gestures, voice, facial expressions) and data-driven design (Vartiainen et al.)
- PRIMM model (Predict–Run–Investigate–Modify–Make) adapted for ML pipelines (PRIMM framework)
- Notional machines for ML differ from traditional programming; neural networks are conceptually different from execution flow of traditional programs (Tedre et al.)
- ML workflows: data collection → data entry → visualization → feature engineering → model building → testing → data permissions (Tedre et al; Srikant & Aggarwal)
- “Low floor, high ceiling”: accessible entry yet nontrivial results—e.g., mushroom identification, color-blind assistance, pose recognition (Tedre et al)

### Curriculum and Pedagogy
- Age-appropriate progression: kindergarten (playful exploration), middle school (experimenting + theory), high school (core knowledge + advanced topics) (Tedre et al.)
- Pedagogical approaches: project-based learning, constructionism, co-design, experiential learning, gamification, active learning (Tedre et al.)
- ML based on math beyond children’s grasp (statistics, probabilistic modeling); challenge of easing entry without advanced math (Tedre et al.)
- CS1/intro programming foundations: curricula, pedagogy, language choice, tools; evidence-based design for novice programmers (Pears et al., Survey of Literature)

### Academic Forecasting
- ML algorithms (KNN, random forest, ANN, BNN) predict academic performance, learning styles, at-risk students (Onyema et al., Prospects and Challenges)
- ML forecasting: pattern recognition, reduced human intervention, accurate forecasting, large-volume data handling, continuous improvement (Onyema et al.)
- Challenges: proneness to errors, data acquisition, time factors, verification challenges (Onyema et al.)

### Misconceptions and Interpretability
- Secondary students’ misconceptions: exactness myth, programming myth, continuous learning, data storage (RWTH study)
- Rudin: stop explaining black-box models; use inherently interpretable models for high-stakes decisions (Stop Explaining Black Box)
- Post-hoc explanations for black boxes are not faithful; can be misleading (e.g., race in recidivism prediction) (Rudin)
- Interpretable models: rule lists, scoring systems, sparse linear models; Rashomon set argument—many accurate models can include interpretable ones (Rudin)

---

## 2. Using AI to Learn and Teach

### AI in Education Applications
- Personalized learning, adaptive learning platforms, intelligent tutoring systems (ITS), virtual facilitators (Elbasi et al., ML in Education)
- ML for administration, instruction enhancement, grade prediction; learning analytics, automated grading (Elbasi et al.)
- AI-powered learning management systems; cloud-based deployment dominant (Elbasi et al.)
- ITSs: detect student progress, adapt content, provide tailored feedback; Duolingo as example (Létourneau et al., ITS K-12 review)
- ITS effects on K-12 learning generally positive but mitigated vs. non-intelligent systems; longer interventions needed (Létourneau et al.)

### Human-in-the-Loop (HITL) and Adaptive Learning
- HITL: students critique AI-generated content via feedback tags; system adapts responses in real time (Tarun et al., HITL Adaptive Learning)
- Student-driven feedback loops: predefined tags (e.g., clarity, correctness, tone) feed into RAG for personalized content (Tarun et al.)
- Teacher-in-the-loop vs. student-in-the-loop; student-centered HITL promotes agency and iterative learning (Tarun et al.)
- “Assessment 4.0”: evidence-rich, non-linear logics; fuzzy systems; metacognition and self-regulation (Fajardo-Ramos et al., Human-in-the-loop assessment)

### Teacher and Student Perceptions
- Teachers vs. students: divergent perceptions of AI’s role in social development (Wen et al., Divergent Role of AI)
- Teachers: AI highly effective for community building in online sessions; students: moderately effective (Wen et al.)
- Students associate AI more with online learning; teachers emphasize AI in physical classrooms for self-confidence, emotional intelligence (Wen et al.)

### AI Literacy and Competency Frameworks
- AILit Framework (OECD/EU): knowledge, skills, attitudes; four domains—Engaging, Creating with, Managing, Designing AI (AILitFramework_ReviewDraft)
- UNESCO AI competency frameworks for K-12; higher education lacks comprehensive AI competency frameworks (UNESCO IESALC)
- 58% of students feel unprepared for AI-driven workforce; 46% think schools adequately prepare them (AILit; UNESCO)
- HCAI-SLR framework: human-centered AI for systematic literature reviews; ethical checkpoints, human oversight (Le Dinh et al.)

---

## 3. Pitfalls

### ML Education Pitfalls
- Shallow learning with “low-floor” apps: children learn workflows, not internal ML mechanisms (Tedre et al.)
- Black-box risk: inaccurate notional machines, unrealistic expectations, anthropomorphizing (Tedre et al.)
- ML models “soft” (probabilistic) and “brittle” (environment/data changes); debugging differs from rule-based programs (Tedre et al.)
- Goodness shifts from correctness to effectiveness; “probably approximately correct” (Tedre et al.)
- Eliza effect: systems appear smarter than warranted; hype cycle risks inflated expectations (Tedre et al.)
- Lack of consensus on ML-CT relationship; research on teaching ML in K-12 in infancy (Tedre et al.)

### Ethical and Bias Pitfalls
- Input bias, system bias, application bias; biases in AI cannot be fully eliminated (Hofmann, Biases in AI)
- IBATA: Injustice, Bad output/outcome, Autonomy, Transformation, Accountability (Hofmann)
- Data privacy, algorithmic bias, transparency, accountability, loss of human interaction (Tojimuxammadov, Ethical Challenges)
- AI as “black boxes”; lack of explainability undermines trust and oversight (Selwyn; Ethical Challenges)
- Overreliance on AI: automation bias, deskilling, reduced critical reflection (Hofmann; CHI Tools for Thought)

### Cognitive and Learning Pitfalls
- GenAI may discourage critical thinking; shift from active information seeking to passive consumption (CHI Tools for Thought)
- “Illusion of comprehensive understanding”; polished AI output minimizes reflective state (Singh et al., CHI)
- Underprepared students benefit least from AI tools; over-reliance when confidence low (Prather & Reeves; CHI)
- Cognitive offloading: students may impede development of metacognitive strategies and schemas (CHI)
- GenAI homogenizes creativity; design fixation on tweaking outputs rather than problem framing (Dalsgaard; CHI)

### Academic Forecasting Pitfalls
- Proneness to errors; biased predictions if training data poor; prediction errors often hard to diagnose (Onyema et al.)
- Data acquisition; time factors; verification challenges; model drift (Onyema et al.)

---

## 4. Agentic AI Systems

### Definitions and Taxonomy
- Agentic AI: goal-directed, autonomous, context-aware; planning, reasoning, memory, tool use (Kostopoulos et al., Agentic AI in Education)
- AI Agents vs. Agentic AI: single-entity tool-assisted vs. multi-agent orchestrated systems (Sapkota et al., AI Agents vs. Agentic AI)
- AI Agents: modular, LLM-driven, narrow task automation; tool integration, prompt engineering (Sapkota et al.)
- Agentic AI: multi-agent collaboration, dynamic task decomposition, persistent memory, orchestrated autonomy (Sapkota et al.)

### Frameworks and Architectures
- Frameworks: CrewAI, LangGraph, AutoGen, Semantic Kernel, Agno, Google ADK, MetaGPT (Derouiche et al., Agentic AI Frameworks)
- ReAct: Reasoning + Acting; chain-of-thought + tool use in iterative loop (Agentic AI Frameworks)
- Memory: short-term (conversation context), long-term (preferences, history); semantic, procedural, episodic (Derouiche et al.)
- Communication protocols: MCP, ACP, A2A, ANP, Agora; JSON-RPC, JSON-LD semantics (Derouiche et al.)

### Agentic AI in Education
- Roles: tutor, coach, companion, teaching assistant, curriculum planner (Kostopoulos et al.)
- Autonomy levels: reactive → adaptive → proactive → collaborative (Kostopoulos et al.)
- Pedagogical foundations: constructivism, social learning, self-regulation, ZPD (Kostopoulos et al.)
- Challenges: bias, lack of transparency, over-reliance, data privacy; need explainability and human oversight (Kostopoulos et al.)

### Agent Challenges
- Hallucination, brittleness, emergent behavior, coordination failure (Sapkota et al.)
- Inter-agent misalignment, error propagation, explainability deficits (Sapkota et al.)
- Solutions: RAG, ReAct loops, orchestration layers, causal modeling (Sapkota et al.)

---

## 5. Hallucinations, Reviewing AI Output, JSON Structure

### Hallucination Types and Causes
- Knowledge-based: factual errors, fabricated citations, wrong dates (Li et al., Mitigating Hallucination)
- Logic-based: flawed reasoning, deduction, induction; correct premises → wrong conclusions (Li et al.)
- Intrinsic vs. extrinsic: contradicts input vs. unverifiable from input (Ji et al.; Zhang & Zhang, RAG Hallucination)
- RAG causes: retrieval failure (data source, query, retriever, strategy); generation deficiency (context noise, conflict, middle curse, alignment) (Zhang & Zhang)

### Mitigation Strategies
- RAG: external knowledge retrieval; grounding; reduces factual errors (Li et al.; Zhang & Zhang)
- Reasoning: CoT, tool-augmented reasoning, symbolic reasoning (Li et al.)
- Agentic systems: RAG + reasoning; unified framework for composite hallucination (Li et al.)
- Prompt engineering: significant role in mitigation; design prompts to reduce hallucinations, automate resource construction (Zhang & Zhang)
- Detection and correction: post-generation mitigation when optimization insufficient (Zhang & Zhang)

### Reviewing AI Output
- Human-in-the-loop assessment: feedback literacy, rubric validation, data interpretation, integrity, orchestration (Fajardo-Ramos et al.)
- Audit trails, explainability practices, data stewardship, communicated assistance limits (Fajardo-Ramos et al.)
- Criterion-anchored prompting, sampling and audits, revision-based workflows (Fajardo-Ramos et al.)

### JSON and Structured Output
- Agent protocols use JSON-RPC, JSON-LD for context exchange (Derouiche et al., Agentic AI Frameworks)
- MCP: structured tool calls via JSON-RPC, schema validation (Agentic AI Frameworks)
- A2A: structured messages (Agent Cards, Task Objects, Artifacts) (Agentic AI Frameworks)
- Structured output formats support machine-readable, validated AI responses (implicit across agent frameworks)

---

## 6. Prompting

### In-Context Learning (ICL)
- Few-shot ICL: models adapt without parameter updates (Brown et al.; Many-Shot ICL paper)
- Many-shot: performance can plateau or decline as demonstrations increase (Zhang et al., DrICL)
- Causes: suboptimal NLL objective; incremental data noise (Zhang et al.)
- DrICL: differentiated learning (many-shot vs. zero-shot); advantage-based reweighting for noisy data (Zhang et al.)
- ICL-50 benchmark: 50 tasks, 1–350 shots, up to 8K tokens (Zhang et al.)

### Prompt Engineering Techniques
- Chain-of-Thought (CoT): explicit reasoning steps; reduces logic-based hallucinations (Li et al.)
- Tool-augmented reasoning: ReAct, Toolformer, PoT (Li et al.)
- Prompt engineering: direct hallucination reduction, automated resource construction, evaluation (Zhang & Zhang)
- Criterion-anchored prompting for assessment (Fajardo-Ramos et al.)

### Prompting in Education
- HITL: student feedback tags drive prompt/RAG adjustments (Tarun et al.)
- Onboarding questionnaires: experience, learning style, goals → personalized prompts (Tarun et al.)
- Experts better at specifying goals, decomposing tasks, prompting effectively (Siu & Fok; CHI Tools for Thought)

### Cognitive and Design Considerations
- Prompting shifts attention from design workflow to “getting AI output” (Quintana & Quintana; CHI)
- GenAI as “instruments of inquiry” vs. risk of design fixation (Dalsgaard; CHI)
- Selective delegation, preserving agency, supporting verification (CHI Tools for Thought)
